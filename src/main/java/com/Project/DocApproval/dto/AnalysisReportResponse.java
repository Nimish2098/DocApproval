package com.Project.DocApproval.dto;

import com.Project.DocApproval.enums.ResumeStatus;

import java.util.List;
import java.util.UUID;

public record AnalysisReportResponse(
        UUID id,
        String candidateName,
        ResumeStatus status,
        Double matchScore,
        List<String> missingSkills,
        String feedback
) {}