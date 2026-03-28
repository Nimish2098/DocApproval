package com.Project.DocApproval.controller;

import com.Project.DocApproval.dto.AnalysisReportResponse;
import com.Project.DocApproval.model.Resume;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.ResumeRepository;
import com.Project.DocApproval.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/results")
@RequiredArgsConstructor
@Tag(name = "Analysis Results", description = "Endpoints for retrieving AI-based resume analysis results and feedback")
public class ResultController {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    @Operation(summary = "Get Analysis Report", description = "Retrieves the NLP analysis report for a specific uploaded resume using its tracking ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved analysis report"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - You do not have permission to view this report"),
        @ApiResponse(responseCode = "404", description = "Resume analysis tracking ID not found")
    })
    @GetMapping("/{trackingId}")
    public ResponseEntity<AnalysisReportResponse> getReport(
            @Parameter(description = "Tracking ID of the resume upload") @PathVariable UUID trackingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        UUID userId = currentUser.getId();
        return resumeRepository.findById(trackingId)
                .map(resume -> {
                    if (!resume.getCandidate().getId().equals(userId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<AnalysisReportResponse>build();
                    }
                    return ResponseEntity.ok(mapToResponse(resume));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private AnalysisReportResponse mapToResponse(Resume resume) {
        String message = switch (resume.getStatus()) {
            case COMPLETED -> generateFeedback(resume.getMatchScore());
            case FAILED -> "The machine failed to read your file. Check if the PDF is corrupt.";
            case UPLOADED, PARSING, ANALYZING -> "The machine is still judging your profile. Refresh in a few seconds.";
        };

        return new AnalysisReportResponse(
                resume.getId(),
                resume.getCandidateName(),
                resume.getStatus(),
                resume.getMatchScore(),
                resume.getMissingSkills(),
                message
        );
    }

    private String generateFeedback(Double score) {
        if (score == null) return "No score calculated yet.";
        if (score < 40) return "Significant skill gaps detected. You're basically a stranger to this JD.";
        if (score < 75) return "Moderate match. You have potential, but need to level up.";
        return "Strong match! Your profile aligns well with the requirements.";
    }
}