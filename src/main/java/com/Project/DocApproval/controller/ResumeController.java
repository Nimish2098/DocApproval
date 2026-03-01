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
            UUID resumeId = resumeService.initiateAnalysis(name, email, file, jdId, userId);

            // 2. Start the background NLP comparison
            resumeService.processResume(resumeId);

            return ResponseEntity.accepted().body(resumeId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}