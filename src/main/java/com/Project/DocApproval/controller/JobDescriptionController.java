package com.Project.DocApproval.controller;

import com.Project.DocApproval.exceptions.InvalidJobDescriptionException;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.UserRepository;
import com.Project.DocApproval.service.JobDescriptionService;
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
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Descriptions", description = "Endpoints for creating and managing job descriptions against which resumes are matched")
public class JobDescriptionController {

    private final JobDescriptionService jdService;
    private final UserRepository userRepository;

    @Operation(summary = "Create Job Description", description = "Creates a new Job Description by accepting either a PDF document or manual text.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Job Description successfully created"),
        @ApiResponse(responseCode = "400", description = "Validation error: Missing title or both file and manual text"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> postJob(
            @Parameter(description = "Title of the job description") @RequestParam("title") String title,
            @Parameter(description = "Optional PDF file containing the JD") @RequestParam(value = "file", required = false) MultipartFile file,
            @Parameter(description = "Optional manual text for the JD") @RequestParam(value = "manualText", required = false) String manualText,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException(("User not found")));
        UUID ownerId = currentUser.getId();
        
        if ((file == null || file.isEmpty()) && (manualText == null || manualText.isBlank())) {
            throw new InvalidJobDescriptionException("You must provide either a PDF file or manual text for the job description.");
        }

        UUID jdId = jdService.createJobDescription(title, file, manualText, ownerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(jdId);
    }
}