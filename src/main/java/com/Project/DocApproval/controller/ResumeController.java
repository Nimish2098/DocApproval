package com.Project.DocApproval.controller;

import com.Project.DocApproval.exceptions.GlobalExceptionHandler;
import com.Project.DocApproval.exceptions.InvalidFileFormatException;
import com.Project.DocApproval.exceptions.InvalidJobDescriptionException;
import com.Project.DocApproval.service.ResumeWorkflowService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final ResumeWorkflowService workflowService;

    /**
     * Entry point for the ATS Checker.
     * Receives the resume and the target JD to initiate the "Honesty" workflow.
     */
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> analyzeResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam("email") String email,
            @RequestParam("name") String name) {

        // 1. Guard: File Validation
        validateFile(file);

        // 2. Guard: JD Validation
        if (jobDescription == null || jobDescription.trim().length() < 50) {
            throw new InvalidJobDescriptionException("Job description is too short to analyze.");
        }

        // 3. Initiate Workflow (Returns UUID for tracking)
        UUID trackingId = workflowService.initiateAtsCheck(name, email, file, jobDescription);

        return ResponseEntity.accepted().body(new ApiResponse(
                "Resume received. The 'Brutally Honest' critique is being generated.",
                trackingId
        ));
    }

    private void validateFile(MultipartFile file) {
        String contentType = file.getContentType();
        boolean isValid = "application/pdf".equals(contentType) ||
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType);

        if (file.isEmpty() || !isValid) {
            throw new InvalidFileFormatException("Only PDF and DOCX files are allowed.");
        }
    }
}