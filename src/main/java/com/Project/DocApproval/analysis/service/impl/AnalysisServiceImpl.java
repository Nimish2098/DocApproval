package com.Project.DocApproval.analysis.service.impl;

import com.Project.DocApproval.analysis.entity.AnalysisResult;
import com.Project.DocApproval.analysis.service.AnalysisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final WebClient webClient;
    private final ObjectMapper mapper;
    private final String apiKey;

    public AnalysisServiceImpl(
            WebClient.Builder webClientBuilder,
            ObjectMapper mapper,
            @Value("${openai.api-key:}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.mapper = mapper;
        this.apiKey = apiKey;
    }

    @Override
    public AnalysisResult performAnalysis(String extractedResumeText, String jobDescriptionText) {
        if (extractedResumeText == null || extractedResumeText.isBlank() || jobDescriptionText == null || jobDescriptionText.isBlank()) {
            return new AnalysisResult(0.0, new ArrayList<>(), "Could not perform analysis: missing data.");
        }

        try {
            if (apiKey.isBlank()) {
                return new AnalysisResult(0.0, new ArrayList<>(),
                        "Analysis unavailable: OPENAI_API_KEY is not configured.");
            }

            String prompt = "You are a resume screening assistant. Evaluate the following resume against the job description "
                    + "using these criteria: (1) Relevant technical skills match, (2) Experience level fit, "
                    + "(3) Project/work relevance, (4) Education relevance, (5) Resume clarity/completeness.\n\n"
                    + "For each criterion, give a score 0-10 and a one-line reason. Also give an overall matchScore (0-100) "
                    + "and a list of missing/weak areas.\n\n"
                    + "Resume: \"" + extractedResumeText + "\"\n"
                    + "Job Description: \"" + jobDescriptionText + "\"\n\n"
                    + "Return ONLY valid JSON in this exact format: "
                    + "{\"criteria\": [{\"name\": \"...\", \"score\": <0-10>, \"reason\": \"...\"}], "
                    + "\"matchScore\": <0-100>, \"missingAreas\": [\"...\"], \"feedback\": \"<one sentence overall verdict>\"}";

            Map<String, Object> body = Map.of(
                    "model", "gpt-4o-mini",
                    "messages", List.of(Map.of("role", "user", "content", prompt))
            );

            String response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = mapper.readTree(response);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            JsonNode result = mapper.readTree(content);

            double score = result.path("matchScore").asDouble();
            List<String> missing = new ArrayList<>();
            result.path("missingAreas").forEach(n -> missing.add(n.asText()));
            String feedback = result.path("feedback").asText();

            // optionally also store the per-criterion breakdown (result.path("criteria"))
            // in a new field/table if you want to show it in the API response

            return new AnalysisResult(score, missing, feedback);

        } catch (Exception e) {
            return new AnalysisResult(0.0, new ArrayList<>(), "Analysis failed: " + e.getMessage());
        }
    }
    @Override
    public String generateFeedback(double score, List<String> missing) {
        if (score < 40) return "Profile Mismatch. Missing key skills: " + String.join(", ", missing);
        if (score < 75) return "Partial Match. Consider improving: " + String.join(", ", missing);
        return "Strong Match. Your profile aligns well with the requirements.";
    }
}