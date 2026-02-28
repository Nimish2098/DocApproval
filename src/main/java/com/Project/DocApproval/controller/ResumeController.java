package com.Project.DocApproval.controller;

import com.Project.DocApproval.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam("name") String name,
            @RequestParam("email") String email) {

        // Basic validation
        if (file.isEmpty() || jobDescription.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        UUID trackingId = null;
        try {
            trackingId = resumeService.initiateAnalysis(name, email, file, jobDescription);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.accepted().body(trackingId);
    }
}