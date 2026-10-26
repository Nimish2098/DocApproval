package com.Project.DocApproval.analysis.dto;

import java.time.Instant;

public record ResumeAnalysisResponse(
    Long id,
    String originalFilename,
    String contentType,
    int overallScore,
    ScoreLabel scoreLabel,
    ResumeReport report,
    Instant createdAt
) {}