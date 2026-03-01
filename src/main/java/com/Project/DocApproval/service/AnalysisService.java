package com.Project.DocApproval.service;

import com.Project.DocApproval.model.AnalysisResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    // Note: This takes the STRING extracted by Tika, not a file path
    public AnalysisResult performAnalysis(String extractedResumeText, Set<String> requiredSkills) {

        // 1. Convert the extracted text to lowercase for case-insensitive matching
        String content = extractedResumeText.toLowerCase();

        // 2. Identify missing skills by checking if the text contains the keyword
        List<String> missingSkills = new ArrayList<>();
        int matchedCount = 0;

        for (String skill : requiredSkills) {
            if (content.contains(skill.toLowerCase())) {
                matchedCount++;
            } else {
                missingSkills.add(skill);
            }
        }

        // 3. Calculate match percentage
        double score = 0.0;
        if (!requiredSkills.isEmpty()) {
            score = ((double) matchedCount / requiredSkills.size()) * 100;
        }

        // 4. Generate the feedback string
        String feedback = generateFeedback(score, missingSkills);

        return new AnalysisResult(score, missingSkills, feedback);
    }

    private String generateFeedback(double score, List<String> missing) {
        if (score < 40) return "Profile Mismatch. Missing: " + String.join(", ", missing);
        if (score < 80) return "Partial Match. Consider learning: " + String.join(", ", missing);
        return "Strong Match. Your profile aligns well.";
    }
}