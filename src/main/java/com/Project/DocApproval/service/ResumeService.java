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

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final StatusHistoryRepository historyRepository;
    private final FileStorageService fileStorageService;
    private final TextExtractionService extractionService;
    private final AnalysisService analysisService;
    @Transactional
    public UUID initiateAnalysis(String name, String email, MultipartFile file, String jd) throws IOException {
        // 1. Physical Write (File System)
        String path = fileStorageService.save(file);

        // 2. Initial Database Write
        Resume resume = new Resume();
        resume.setCandidateName(name);
        resume.setEmail(email);
        resume.setFilePath(path);
        resume.setTargetJobDescription(jd);
        resume.setStatus(ApplicationStatus.UPLOADED);

        Resume savedResume = resumeRepository.save(resume);

        // 3. State Machine Log (The History)
        recordStatus(savedResume, ApplicationStatus.UPLOADED, "Resume successfully uploaded.");

        return savedResume.getId();
    }

    private void recordStatus(Resume resume, ApplicationStatus status, String comment) {
        ResumeStatusHistory history = new ResumeStatusHistory();
        history.setResume(resume);
        history.setStatus(status);
        history.setStatusComment(comment);
        historyRepository.save(history);
    }

    @Async
    @Transactional
    public void processAnalysis(UUID resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(()-> new RuntimeException("Not Found"));

        try {
            // Update State: PARSING
            recordStatus(resume, ApplicationStatus.PARSING, "Extracting text from document.");
            String extractedText = extractionService.extractText(resume.getFilePath());

            // Update State: ANALYZING
            recordStatus(resume, ApplicationStatus.ANALYZING, "Comparing against Job Description.");
            AnalysisResult result = analysisService.performAnalysis(extractedText, resume.getTargetJobDescription());

            // Save Results & Complete
            resume.setMatchScore(result.score());
            resume.setMissingSkills(result.missingKeywords());
            resume.setStatus(ApplicationStatus.COMPLETED);

            resumeRepository.save(resume);
            recordStatus(resume, ApplicationStatus.COMPLETED, "Analysis finalized.");

        } catch (Exception e) {
            resume.setStatus(ApplicationStatus.FAILED);
            resumeRepository.save(resume);
            recordStatus(resume, ApplicationStatus.FAILED, "Error: " + e.getMessage());
        }
    }

    @Async // Runs in background so the user gets the UUID immediately
    @Transactional
    public void processResume(UUID resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow();

        try {
            // Transition to PARSING
            updateStatus(resume, ApplicationStatus.PARSING, "Extracting text from document.");
            String text = extractionService.extractText(resume.getFilePath());

            // Transition to ANALYZING
            updateStatus(resume, ApplicationStatus.ANALYZING, "Comparing against JD.");
            AnalysisResult result = analysisService.performAnalysis(text, resume.getTargetJobDescription());

            // Transition to COMPLETED
            resume.setMatchScore(result.score());
            resume.setMissingSkills(result.missingKeywords());
            resume.setStatus(ApplicationStatus.COMPLETED);
            resumeRepository.save(resume);

            updateStatus(resume, ApplicationStatus.COMPLETED, "Analysis successful.");

        } catch (Exception e) {
            updateStatus(resume, ApplicationStatus.FAILED, "Error: " + e.getMessage());
        }
    }
    /**
     * Updates the current status of the Resume and logs the change in the history table.
     */
    @Transactional
    private void updateStatus(Resume resume, ApplicationStatus status, String comment) {
        // 1. Update the main Resume entity
        resume.setStatus(status);
        resumeRepository.save(resume);

        // 2. Create and save the history record
        ResumeStatusHistory history = new ResumeStatusHistory();
        history.setResume(resume);
        history.setStatus(status);
        history.setStatusComment(comment);
        history.setLocalDateTime(LocalDateTime.now()); // Ensure this matches your entity field name

        historyRepository.save(history);

        log.info("Resume ID {}: Status changed to {} - {}", resume.getId(), status, comment);
    }
}