package com.Project.DocApproval.analysis.service;

import com.Project.DocApproval.analysis.entity.AnalysisResult;

import java.util.List;

public interface AnalysisService {
    AnalysisResult performAnalysis(String extractedResumeText, String jobDescriptionText);

    String generateFeedback(double score, List<String> missing);
}
