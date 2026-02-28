package com.Project.DocApproval.model;

import java.util.List;


public record AnalysisResult(
        double score,
        List<String> missingKeywords
) {}
