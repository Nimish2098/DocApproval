package com.Project.DocApproval.controller;

import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.UserRepository;
import com.Project.DocApproval.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
@Tag(name = "Resume Processing", description = "Endpoints for uploading and analyzing candidate resumes against Job Descriptions")
public class ResumeController {

    private final ResumeService resumeServiceImpl;
    private final UserRepository userRepository;

    @Operation(summary = "Upload and Analyze Resume", description = "Uploads a resume file and initiates a background AI matching process against a specified Job Description.")
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Resume accepted for background processing. Returns a tracking ID."),
        @ApiResponse(responseCode = "400", description = "Invalid request payload or missing file"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "500", description = "Internal server error saving the physical file")
    })
    @PostMapping(value = "/upload/{jdId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> uploadResume(
            @Parameter(description = "UUID of the target Job Description") @PathVariable UUID jdId,
            @Parameter(description = "Candidate's resume file (PDF)") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Candidate's name") @RequestParam("name") String name,
            @Parameter(description = "Candidate's email") @RequestParam("email") String email,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User currentUser = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UUID userId = currentUser.getId();
            UUID resumeId = resumeServiceImpl.initiateAnalysis(name, email, file, jdId, userId);

            // 2. Start the background NLP comparison
            // Being @Async, this runs in a separate thread
            resumeServiceImpl.processResume(resumeId);

            // 202 Accepted is the correct status for background tasks
            return ResponseEntity.accepted().body(resumeId);

        } catch (IOException e) {
            // This is caught if the physical file save fails
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}