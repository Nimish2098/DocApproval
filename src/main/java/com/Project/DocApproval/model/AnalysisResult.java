package com.Project.DocApproval.model;

import java.util.List;



public record AnalysisResult(
        double matchScore,
        List<String> missingSkills,
        String analysisFeedback
) {}