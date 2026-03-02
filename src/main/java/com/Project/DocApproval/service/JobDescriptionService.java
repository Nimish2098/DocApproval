package com.Project.DocApproval.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface JobDescriptionService {
    @Transactional
    UUID createJobDescription(String title, MultipartFile file, String manualText, UUID ownerId) throws IOException;
}
