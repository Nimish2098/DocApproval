package com.Project.DocApproval.controller;

import com.Project.DocApproval.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(value = "/upload/{jdId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> uploadResume(
            @PathVariable UUID jdId,
            @RequestHeader("X-User-Id") UUID userId, // Simulated Auth Header
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("email") String email) {

        try {
            // 1. Initiate the record and link User + JD
            // This will throw DuplicateApplicationException if they apply twice
            UUID resumeId = resumeService.initiateAnalysis(name, email, file, jdId, userId);

            // 2. Start the background NLP comparison
            // Being @Async, this runs in a separate thread
            resumeService.processResume(resumeId);

            // 202 Accepted is the correct status for background tasks
            return ResponseEntity.accepted().body(resumeId);

        } catch (IOException e) {
            // This is caught if the physical file save fails
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}