package com.Project.DocApproval.controller;

import com.Project.DocApproval.dto.AnalysisReportResponse;
import com.Project.DocApproval.enums.ApplicationStatus;
import com.Project.DocApproval.model.Resume;
import com.Project.DocApproval.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResumeRepository resumeRepository;

    @GetMapping("/{trackingId}")
    public ResponseEntity<AnalysisReportResponse> getReport(@PathVariable UUID trackingId) {
        return resumeRepository.findById(trackingId)
                .map(this::mapToResponse) // Clean hand-off to the helper method
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Converts the database Entity into a clean JSON Response for the User.
     */
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
                resume.getMatchScore(), // This will be null until COMPLETED
                resume.getMissingSkills(),
                message
        );
    }

    private String generateFeedback(Double score) {
        if (score == null) return "No score calculated yet.";
        if (score < 40) return "Significant skill gaps detected. You're basically a stranger to this JD.";
        if (score < 75) return "Moderate match. You have potential, but need to level up.";
        return "Strong match! You and this JD are a match made in heaven.";
    }
}