package com.Project.DocApproval.analysis.dto;

public record ActionPlanStep(
    int priority,         // 1 = do first
    String title,
    String details,
    String timeEstimate,  // e.g. "1 day", "2 hours"
    Impact impact
) {}
