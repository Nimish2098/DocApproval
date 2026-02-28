package com.Project.DocApproval.controller;

import com.Project.DocApproval.dto.AnalysisReportResponse;
import com.Project.DocApproval.enums.ApplicationStatus;
import com.Project.DocApproval.model.Resume;
import com.Project.DocApproval.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResumeRepository resumeRepository;

    @GetMapping("/{trackingId}")
    public ResponseEntity<AnalysisReportResponse> getReport(@PathVariable UUID trackingId) {
        return resumeRepository.findById(trackingId)
                .map(resume -> {
                    // Create a friendly feedback message based on the score
                    String feedback = generateFeedback(resume.getMatchScore(), resume.getStatus());

                    AnalysisReportResponse response = new AnalysisReportResponse(
                            resume.getId(),
                            resume.getCandidateName(),
                            resume.getStatus(),
                            resume.getMatchScore(),
                            resume.getMissingSkills(),
                            feedback
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    private AnalysisReportResponse mapToResponse(Resume resume) {
        String message = switch (resume.getStatus()) {
            case COMPLETED -> "Analysis finished. See your skill gaps below.";
            case FAILED -> "Something went wrong during analysis.";
            case UPLOADED, PARSING, ANALYZING -> "The machine is still judging your profile. Please wait.";
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
    private String generateFeedback(Double score, ApplicationStatus status) {
        if (status != ApplicationStatus.COMPLETED) {
            return "Analysis is still in progress. Please check back in a few seconds.";
        }
        if (score == null) return "No score calculated.";
        if (score < 40) return "Significant skill gaps detected. Focus on the missing keywords below.";
        if (score < 75) return "Moderate match. Consider adding the missing skills to your resume.";
        return "Strong match! Your resume aligns well with this job description.";
    }
}
