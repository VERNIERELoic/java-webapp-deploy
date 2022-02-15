package fr.jco.ign.ban.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.jco.ign.ban.dto.AddressDTO;
import fr.jco.ign.ban.dto.JobResultDTO;
import fr.jco.ign.ban.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class AddressController {

    private final AddressService service;

    @GetMapping
    public List<AddressDTO> search(@RequestParam("terms") String terms) {
        return this.service.search(Pageable.ofSize(20), terms);
    }

    @PostMapping
    public ResponseEntity<List<JobResultDTO>> uploadFile(@RequestParam MultipartFile[] file) {
        return this.service.insertBatch(Arrays.asList(file));
    }

    @PostMapping("/default-file")
    public ResponseEntity<JobResultDTO> importDefaultFile() {
        return this.service.importDefaultFile();
    }
}
