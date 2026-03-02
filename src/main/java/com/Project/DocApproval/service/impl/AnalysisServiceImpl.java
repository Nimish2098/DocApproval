package com.Project.DocApproval.service.impl;

import com.Project.DocApproval.model.AnalysisResult;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AnalysisServiceImpl implements com.Project.DocApproval.service.AnalysisService {

    @Override
    public AnalysisResult performAnalysis(String extractedResumeText, Set<String> requiredSkills) {
        // 1. Safety Check: If Tika returned nothing or JD has no keywords, score is 0
        if (extractedResumeText == null || extractedResumeText.isBlank() || requiredSkills == null || requiredSkills.isEmpty()) {
            return new AnalysisResult(0.0, new ArrayList<>(requiredSkills != null ? requiredSkills : Collections.emptySet()), "Could not perform analysis: missing data.");
        }

        String content = extractedResumeText.toLowerCase();
        List<String> missingSkills = new ArrayList<>();
        int matchedCount = 0;

        // 2. The Core Comparison Logic
        for (String skill : requiredSkills) {
            // Check if the JD keyword exists anywhere in the resume text
            if (content.contains(skill.toLowerCase())) {
                matchedCount++;
            } else {
                missingSkills.add(skill);
            }
        }

        // 3. Calculate match percentage
        double score = ((double) matchedCount / requiredSkills.size()) * 100;

        String feedback = generateFeedback(score, missingSkills);

        // 4. Return the record with names matching your ResumeServiceImpl calls
        return new AnalysisResult(score, missingSkills, feedback);
    }

    @Override
    public String generateFeedback(double score, List<String> missing) {
        if (score < 40) return "Profile Mismatch. Missing key skills: " + String.join(", ", missing);
        if (score < 75) return "Partial Match. Consider improving: " + String.join(", ", missing);
        return "Strong Match. Your profile aligns well with the requirements.";
    }
}