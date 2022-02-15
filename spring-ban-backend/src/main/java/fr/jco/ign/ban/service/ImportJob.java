package fr.jco.ign.ban.service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.sql.DataSource;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import fr.jco.ign.ban.dto.AddressCSV;
import fr.jco.ign.ban.dto.AddressToSaveDTO;
import fr.jco.ign.ban.model.Address;
import fr.jco.ign.ban.model.ElasticAddress;
import fr.jco.ign.ban.repository.ElasticAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class ImportJob {

    private final JobBuilderFactory jobFactory;

    private final StepBuilderFactory stepFactory;

    @Value("${jco.pool-size}")
    private Integer poolSize;

    @Bean
    public Job addressImportJob(Step splitStep, Step principalStepImport) {
        return this.jobFactory.get("addressImportJob")
            .incrementer(new RunIdIncrementer())
            .start(splitStep)
            .next(principalStepImport)
            .build();
    }

    @Bean
    public Step splitStep(Tasklet splitCommandTasklet) {
        return this.stepFactory.get("splitStep")
            .tasklet(splitCommandTasklet)
            .build();
    }

    @Bean
    @StepScope
    public SystemCommandTasklet splitCommandTasklet(@Value("#{jobParameters['filePath']}") String filePath,
        @Value("#{jobParameters['workDir']}") String workDir) {
        SystemCommandTasklet tasklet = new SystemCommandTasklet();
        String command = String.format("split --numeric-suffixes=1 --additional-suffix .csv --number=l/%d %s %s-", poolSize, filePath, workDir);
        tasklet.setCommand(command);
        tasklet.setTimeout(20000);
        return tasklet;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(poolSize);
        taskExecutor.setCorePoolSize(poolSize);
        taskExecutor.setQueueCapacity(poolSize);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Bean
    public Step principalStepImport(Partitioner perFilePartitioner, Step workerStepImport, StepExecutionListener stepExecutionListener) {
        return this.stepFactory.get("principalStepImport")
            .partitioner("workerStepImport", perFilePartitioner)
            .step(workerStepImport)
            .listener(stepExecutionListener)
            .taskExecutor(taskExecutor())
            .build();
    }

    @Bean
    @StepScope
    public Partitioner perFilePartitioner(@Value("#{jobParameters['workDir']}") String workDir) {
        return gridSize -> {
            Map<String, ExecutionContext> map = new HashMap<>(gridSize);
            IntStream.rangeClosed(1, poolSize).forEach(i -> {
                ExecutionContext context = new ExecutionContext();
                context.putString("workingFile", String.format("%s-%02d.csv", workDir, i));
                map.put(String.valueOf(i), context);
            });
            return map;
        };
    }

    @Bean
    public Step workerStepImport(ItemReader<AddressCSV> readerCsv, ItemProcessor<AddressCSV, AddressToSaveDTO> processorCsv,
        ItemWriter<AddressToSaveDTO> someWriter, SkipListener<AddressCSV, AddressToSaveDTO> skipListener) {
        return this.stepFactory.get("workerStepImport")
            .<AddressCSV, AddressToSaveDTO>chunk(1000)
            .faultTolerant()
            .skip(FlatFileParseException.class)
            .skipLimit(Integer.MAX_VALUE)
            .listener(skipListener)
            .reader(readerCsv)
            .processor(processorCsv)
            .writer(someWriter)
            .build();

    }

    @Bean
    @StepScope
    public FlatFileItemReader<AddressCSV> readerCsv(@Value("#{stepExecutionContext['workingFile']}") String workingFile) {
        return new FlatFileItemReaderBuilder<AddressCSV>()
            .name("readerCsv")
            .resource(new FileSystemResource(workingFile))
            .delimited()
            .delimiter(";")
            .names(new String[]{"id", "id_fantoir", "numero", "rep", "nom_voie", "code_postal", "code_insee", "nom_commune",
                "code_insee_ancienne_commune", "nom_ancienne_commune", "x", "y", "lon", "lat", "type_position", "alias", "nom_ld",
                "libelle_acheminement", "nom_afnor",
                "source_position", "source_nom_voie", "certification_commune"})
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(AddressCSV.class);
            }})
            .build();
    }

    @Bean
    public ItemProcessor<AddressCSV, AddressToSaveDTO> processorCsv() {
        return addressCSV -> {
            try {
                return new AddressToSaveDTO(addressCSV);
            } catch (NumberFormatException e) {
                log.warn("[SKIP ITEM] Raison : {}, Item = {}", e.getMessage(), addressCSV);
                return null;
            }
        };
    }


    @Bean
    @Profile("!elastic")
    public ItemWriter<AddressToSaveDTO> upsertPostgresWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<AddressToSaveDTO>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO address (id, id_fantoir, numero, nom_voie, zip_code, insee_code, city_name, longitude, latitude, label_forwarding, "
                + Address.COLUMN_SEARCH_NAME + ") "
                + "VALUES (:id, :idFantoir, :numero, :nomVoie, :zipCode, :inseeCode, :cityName, :longitude, :latitude, :labelForwarding, :searchName) "
                + "ON CONFLICT(id) DO UPDATE SET "
                + "id_fantoir = :idFantoir, "
                + "numero = :numero, "
                + "nom_voie = :nomVoie, "
                + "zip_code = :zipCode, "
                + "insee_code = :inseeCode, "
                + "city_name = :cityName, "
                + "longitude = :longitude, "
                + "latitude = :latitude, "
                + "label_forwarding = :labelForwarding, "
                + Address.COLUMN_SEARCH_NAME + " = :searchName"
            )
            .dataSource(dataSource)
            .build();
    }

    @Bean
    @Profile("elastic")
    public ItemWriter<AddressToSaveDTO> elasticWriter(ElasticAddressRepository repository) {
        return list -> repository.saveAll(list.parallelStream().map(ElasticAddress::new).collect(Collectors.toList()));
    }

    @Bean
    @StepScope
    StepExecutionListener stepExecutionListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {

            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                String filePath = stepExecution.getJobExecution().getJobParameters().getString("filePath");
                String originalFilename = stepExecution.getJobExecution().getJobParameters().getString("originalFilename");
                Date startTime = stepExecution.getStartTime();
                Date endTime = new Date();
                int writeCount = stepExecution.getWriteCount();
                Duration duration = Duration.ofMillis(endTime.getTime() - startTime.getTime());
                log.info("Traitement du fichier : originalFilename = {}, path = {}, startTime = {}, endTime = {}, writeCount = {}, duration = {}",
                    originalFilename, filePath, startTime, endTime, writeCount, duration);
                return stepExecution.getExitStatus();
            }
        };
    }

    @Bean
    SkipListener<AddressCSV, AddressToSaveDTO> skipListener() {
        return new SkipListener<>() {
            @Override
            public void onSkipInRead(Throwable t) {
                if (t instanceof FlatFileParseException) {
                    log.warn("READ : {},{}", ((FlatFileParseException) t).getInput(), t.getMessage());
                    return;
                }
                log.warn("READ : {}", t.getMessage());
            }

            @Override
            public void onSkipInWrite(AddressToSaveDTO dto, Throwable t) {
                log.warn("WRITE : {} = {}", t.getMessage(), dto);
            }

            @Override
            public void onSkipInProcess(AddressCSV dto, Throwable t) {
                log.warn("PROCESS : {} = {}", t.getMessage(), dto);
            }
        };
    }
}
