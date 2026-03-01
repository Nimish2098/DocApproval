package com.Project.DocApproval.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jd")
@RequiredArgsConstructor
public class JobDescriptionController {

    private final JobDescriptionService jdService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> setupJob(
            @RequestParam("title") String title,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "manualText", required = false) String manualText) {

        try {
            // Returns the UUID for the 'Truth-Machine' to reference later
            UUID id = jdService.createJobDescription(title, file, manualText);
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}