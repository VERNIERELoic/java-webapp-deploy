package fr.jco.ign.ban.service;

import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.ok;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fr.jco.ign.ban.dao.AbstractAddressDAO;
import fr.jco.ign.ban.dto.AddressDTO;
import fr.jco.ign.ban.dto.JobResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AbstractAddressDAO repo;

    private final Job addressImportJob;

    private final JobLauncher jobLauncher;

    @Value("${jco.pool-size}")
    private Integer poolSize;

    @Value("${jco.default-file}")
    private String defaultFile;

    public List<AddressDTO> search(Pageable pageable, String terms) {
        return this.repo.findAll(pageable, terms.toLowerCase(Locale.ROOT)).getContent();
    }

    public ResponseEntity<List<JobResultDTO>> insertBatch(List<MultipartFile> files) {
        log.info("Tentative d'import des fichiers suivants : {}", String.join(", ",
            files.stream().map(MultipartFile::getOriginalFilename).collect(Collectors.toList())));
        List<JobResultDTO> result = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String workDir = String.format("/tmp/%s", UUID.randomUUID());
                String tmpFile = String.format("%s.csv", workDir);
                log.debug("Transfert du fichier {} vers {}", file.getOriginalFilename(), tmpFile);
                file.transferTo(Path.of(tmpFile));
                result.add(this.launchBatch(workDir, tmpFile, file.getOriginalFilename()));
            }
        } catch (IOException e) {
            log.error("Impossible de transférer le fichier jusqu'au serveur", e);
            return internalServerError()
                .body(List.of(JobResultDTO.builder()
                    .details("Impossible de transférer le fichier jusqu'au serveur")
                    .errors(Collections.singletonList(e.getMessage()))
                    .exitStatus("NONE")
                    .build()));
        }
        return ok(result);
    }

    private JobResultDTO launchBatch(String workDir, String tmpFile, String originalFilename) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("launchTime", LocalDateTime.now().toString())
                .addString("filePath", tmpFile)
                .addString("workDir", workDir)
                .addString("originalFilename", originalFilename).toJobParameters();
            log.info("Lancement de l'import du fichier : {}", originalFilename);
            JobExecution run = jobLauncher.run(this.addressImportJob, jobParameters);
            List<String> failureMessages = run.getAllFailureExceptions()
                .stream()
                .map(Throwable::getMessage)
                .collect(Collectors.toList());

            JobResultDTO resultDTO = JobResultDTO.builder()
                .details("-")
                .duration(Duration.ofMillis(run.getEndTime().getTime() - run.getStartTime().getTime()).toString().substring(2)
                    .replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase())
                .skipReadCount(
                    run.getStepExecutions().stream().filter(this::isNotWorkerStep).map(StepExecution::getReadSkipCount).reduce(0, Integer::sum))
                .writeCount(run.getStepExecutions().stream().filter(this::isNotWorkerStep).map(StepExecution::getWriteCount).reduce(0, Integer::sum))
                .errors(failureMessages)
                .exitStatus(run.getExitStatus().getExitCode())
                .file(originalFilename)
                .build();
            log.debug("Résultat de l'import : {}", resultDTO);
            return resultDTO;
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error("Erreur durant l'import du fichier", e);
            return JobResultDTO.builder()
                .details("Erreur durant l'import du fichier")
                .errors(Collections.singletonList(e.getMessage()))
                .exitStatus(ExitStatus.UNKNOWN.getExitCode())
                .build();
        } finally {
            try {
                log.debug("Tente de supprimer le fichier original : {} ({})", originalFilename, tmpFile);
                Files.deleteIfExists(Path.of(tmpFile));
                for (int i = 1; i <= poolSize; i++) {
                    String workingFile = String.format("%s-%02d.csv", workDir, i);
                    log.debug("Tente de supprimer le fichier de travail : {}", workingFile);
                    Files.deleteIfExists(Path.of(workingFile));
                }
            } catch (IOException e) {
                log.warn("Impossible de nettoyer les fichiers", e);
            }
        }
    }

    private boolean isNotWorkerStep(StepExecution stepExecution) {
        return !stepExecution.getStepName().contains("worker");
    }

    public ResponseEntity<JobResultDTO> importDefaultFile() {
        Path path = Path.of(defaultFile);
        if (!Files.exists(path) || !Files.isReadable(path)) {
            log.warn("Le fichier {} ne peut être importée: exist = {}, canRead = {}", defaultFile, Files.exists(path), Files.isReadable(path));
            return ok(JobResultDTO.builder().file(defaultFile).details("Impossible d'importer le fichier").build());
        }
        return ok(this.launchBatch(defaultFile, defaultFile, defaultFile));
    }
}
