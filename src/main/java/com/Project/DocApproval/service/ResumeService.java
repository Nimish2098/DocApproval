package com.Project.DocApproval.service;

import com.Project.DocApproval.enums.ResumeStatus;
import com.Project.DocApproval.model.Resume;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ResumeService {
    @Transactional
    UUID initiateAnalysis(String name, String email, MultipartFile file, UUID jdId, UUID userId) throws IOException;

    @Async
    @Transactional
    void processResume(UUID resumeId);

    void updateStatus(Resume resume, ResumeStatus status, String comment);
}
