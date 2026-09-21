package com.Project.DocApproval.analysis.entity;

import java.util.List;



public record AnalysisResult(
        double matchScore,
        List<String> missingSkills,
        String analysisFeedback
) {}