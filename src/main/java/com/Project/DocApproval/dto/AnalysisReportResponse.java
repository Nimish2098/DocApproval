package com.Project.DocApproval.dto;

import com.Project.DocApproval.enums.ApplicationStatus;

import java.util.List;
import java.util.UUID;

public record AnalysisReportResponse(
        UUID id,
        String candidateName,
        ApplicationStatus status,
        Double matchScore,
        List<String> missingSkills,
        String feedback
) {}