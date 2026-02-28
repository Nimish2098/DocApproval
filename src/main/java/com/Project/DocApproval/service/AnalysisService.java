package com.Project.DocApproval.service;

import com.Project.DocApproval.model.AnalysisResult;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    public AnalysisResult performAnalysis(String resumeText, String jdText) {
        String cleanResume = resumeText.toLowerCase();

        // 1. Dynamic Rubric Generation
        Set<String> jdKeywords = extractKeywords(jdText);

        // 2. Set Subtraction (Finding the Voids)
        List<String> missingKeywords = jdKeywords.stream()
                .filter(keyword -> !cleanResume.contains(keyword))
                .collect(Collectors.toList());

        // 3. Score Calculation
        double score = calculateScore(jdKeywords.size(), missingKeywords.size());

        return new AnalysisResult(score, missingKeywords);
    }

    private Set<String> extractKeywords(String text) {
        // Simple regex to find potential tech/professional terms (4+ chars)
        // and filter out common filler words.
        return Arrays.stream(text.toLowerCase().split("[\\s,.:;()]+"))
                .filter(word -> word.length() > 3)
                .filter(word -> !isStopWord(word))
                .collect(Collectors.toSet());
    }

    private boolean isStopWord(String word) {
        return List.of("this", "that", "with", "from", "your", "must", "have").contains(word);
    }

    private double calculateScore(int total, int missing) {
        if (total == 0) return 0.0;
        return ((double) (total - missing) / total) * 100;
    }
}