package com.Project.DocApproval.service;

import com.Project.DocApproval.model.AnalysisResult;

import java.util.List;
import java.util.Set;

public interface AnalysisService {
    AnalysisResult performAnalysis(String extractedResumeText, Set<String> requiredSkills);

    String generateFeedback(double score, List<String> missing);
}
