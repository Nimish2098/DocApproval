package com.Project.DocApproval.analysis.service.impl;

import com.Project.DocApproval.analysis.entity.AnalysisResult;
import com.Project.DocApproval.analysis.service.AnalysisService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final ChatClient chatClient;

    // ChatClient.Builder is automatically provided and configured by Spring AI
    public AnalysisServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public AnalysisResult performAnalysis(String extractedResumeText, String jobDescriptionText) {
        if (extractedResumeText == null || extractedResumeText.isBlank()
                || jobDescriptionText == null || jobDescriptionText.isBlank()) {
            return new AnalysisResult(0.0, List.of(), "Could not perform analysis: missing data.");
        }

        try {
            String systemPrompt = """
                You are an expert ATS (Applicant Tracking System) and resume evaluator. 
                Evaluate the provided resume against the job description strictly using these criteria:
                1. Relevant technical skills match
                2. Experience level fit
                3. Project/work relevance
                4. Education relevance
                5. Resume clarity/completeness
                """;

            String userPrompt = """
                Resume:
                {resume}

                Job Description:
                {jd}
                """;

            // .entity() automatically forces structured output and handles JSON deserialization
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(u -> u.text(userPrompt)
                            .param("resume", extractedResumeText)
                            .param("jd", jobDescriptionText))
                    .call()
                    .entity(AnalysisResult.class);

        } catch (Exception e) {
            return new AnalysisResult(0.0, List.of(), "Analysis failed: " + e.getMessage());
        }
    }

    @Override
    public String generateFeedback(double score, List<String> missing) {
        if (score < 40) return "Profile Mismatch. Missing key skills: " + String.join(", ", missing);
        if (score < 75) return "Partial Match. Consider improving: " + String.join(", ", missing);
        return "Strong Match. Your profile aligns well with the requirements.";
    }
}