package com.Project.DocApproval.controller;

import com.Project.DocApproval.service.JobDescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobDescriptionController {

    private final JobDescriptionService jdService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> postJob(
            @RequestParam("title") String title,
            // Make file optional
            @RequestParam(value = "file", required = false) MultipartFile file,
            // Add optional manual text
            @RequestParam(value = "manualText", required = false) String manualText,
            @RequestHeader("X-User-Id") UUID ownerId) {

        try {
            // Check if both are missing
            if ((file == null || file.isEmpty()) && (manualText == null || manualText.isBlank())) {
                return ResponseEntity.badRequest().build();
            }

            UUID jdId = jdService.createJobDescription(title, file, manualText, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(jdId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}