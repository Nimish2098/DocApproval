package com.Project.DocApproval.service;

import com.Project.DocApproval.enums.ApplicationStatus;
import com.Project.DocApproval.model.Resume;
import com.Project.DocApproval.model.ResumeStatusHistory;
import com.Project.DocApproval.repository.ResumeRepository;
import com.Project.DocApproval.repository.StatusHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeWorkflowService {

    private final ResumeRepository resumeRepository;
    private final StatusHistoryRepository historyRepository;
    private final FileStorageService fileStorageService; // For saving to disk/S3
    private final AIService aiService; // We will build this in Phase 2

    /**
     * Entry point for the roadmap's Step 4: Initial Write
     */
    @Transactional
    public UUID initiateAtsCheck(String name, String email, MultipartFile file, String jdText) {
        // 1. Store file and get path (Handles IOException internally)
        String filePath = fileStorageService.store(file);

        // 2. Create the Resume Entity (Status: UPLOADED)
        Resume resume = new Resume();
        resume.setCandidateName(name);
        resume.setEmail(email);
        resume.setFilePath(filePath);
        resume.setTargetJobDescription(jdText);
        resume.setStatus(ApplicationStatus.UPLOADED);

        Resume savedResume = resumeRepository.save(resume);

        // 3. Log initial status in the audit trail
        recordStatusChange(savedResume, ApplicationStatus.UPLOADED, "Application initiated by user.");

        // 4. Roadmap Phase 2 Trigger: Async AI Critique
        // This ensures the user gets their Tracking ID immediately.
        CompletableFuture.runAsync(() -> runAIAttack(savedResume.getId()));

        return savedResume.getId();
    }

    private void runAIAttack(UUID resumeId) {
        try {
            // This will move through ANALYZING -> CRITIQUING -> FEEDBACK_GENERATED
            aiService.performCritique(resumeId);
        } catch (Exception e) {
            log.error("AI Workflow failed for {}: {}", resumeId, e.getMessage());
            updateStatus(resumeId, ApplicationStatus.FAILED_PROCESSING, "AI Service Timeout.");
        }
    }

    private void recordStatusChange(Resume resume, ApplicationStatus status, String comment) {
        ResumeStatusHistory history = new ResumeStatusHistory();
        history.setResume(resume);
        history.setStatus(status);
        history.setComment(comment);
        historyRepository.save(history);
    }
}