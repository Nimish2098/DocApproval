package com.Project.DocApproval.service;

import com.Project.DocApproval.model.AnalysisResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final NlpService nlpservice;

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

    public Set<String> extractKeywords(String text) {
        // Only keep words tagged as NN (Noun) or NNP (Proper Noun)
        // This automatically ignores "where", "into", "highly", etc.
        return nlpservice.extractTechnicalNouns(text);
    }
    private boolean isStopWord(String word) {
        return List.of("this", "that", "with", "from", "your", "must", "have").contains(word);
    }

    private double calculateScore(int total, int missing) {
        if (total == 0) return 0.0;
        return ((double) (total - missing) / total) * 100;
    }
}