package com.Project.DocApproval.service;

import com.Project.DocApproval.enums.ApplicationStatus;
import com.Project.DocApproval.model.AnalysisResult;
import com.Project.DocApproval.model.Resume;
import com.Project.DocApproval.model.ResumeStatusHistory;
import com.Project.DocApproval.repository.ResumeRepository;
import com.Project.DocApproval.repository.StatusHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import com.Project.DocApproval.model.*;
import com.Project.DocApproval.repository.*;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jdRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final TextExtractionService extractionService;
    private final AnalysisService analysisService;
    private final StatusHistoryRepository historyRepository;

    @Transactional
    public UUID initiateAnalysis(String name, String email, MultipartFile file, UUID jdId, UUID userId) throws IOException {
        // 1. Fetch parents
        User user = userRepository.findById(userId).orElseThrow();
        JobDescription jd = jdRepository.findById(jdId).orElseThrow();

        // 2. Save physical file
        String path = fileStorageService.save(file, "resumes");

        // 3. Map everything to the Resume entity
        Resume resume = new Resume();
        resume.setCandidateName(name);
        resume.setEmail(email);
        resume.setFilePath(path);
        resume.setCandidate(user); // Mapping User
        resume.setJobDescription(jd); // Mapping JD
        resume.setStatus(ApplicationStatus.UPLOADED);

        Resume saved = resumeRepository.save(resume);
        updateStatus(saved, ApplicationStatus.UPLOADED, "Initial upload complete.");

        return saved.getId();
    }

    @Async
    @Transactional
    public void processResume(UUID resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow();

        try {
            updateStatus(resume, ApplicationStatus.PARSING, "Extracting text.");
            String text = extractionService.extractText(resume.getFilePath());

            updateStatus(resume, ApplicationStatus.ANALYZING, "Comparing against JD requirements.");

            // Get pre-analyzed keywords from the linked JD
            Set<String> requiredSkills = resume.getJobDescription().getExtractedKeywords();

            // Run the Analysis
            AnalysisResult result = analysisService.performAnalysis(text, requiredSkills);

            // Update with results
            resume.setMatchScore(result.matchPercentage());
            resume.setMissingSkills(result.missingSkills());
            resume.setAnalysisFeedback(result.evaluationFeedback());
            resume.setStatus(ApplicationStatus.COMPLETED);

            resumeRepository.save(resume);
            updateStatus(resume, ApplicationStatus.COMPLETED, "Analysis successful.");

        } catch (Exception e) {
            updateStatus(resume, ApplicationStatus.FAILED, "Error: " + e.getMessage());
        }
    }

    private void updateStatus(Resume resume, ApplicationStatus status, String comment) {
        resume.setStatus(status);
        resumeRepository.save(resume);

        ResumeStatusHistory history = new ResumeStatusHistory();
        history.setResume(resume);
        history.setStatus(status);
        history.setStatusComment(comment);
        history.setLocalDateTime(LocalDateTime.now());
        historyRepository.save(history);
    }
}