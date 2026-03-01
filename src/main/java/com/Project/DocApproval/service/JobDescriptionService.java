package com.Project.DocApproval.service;

import com.Project.DocApproval.model.JobDescription;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.JobDescriptionRepository;
import com.Project.DocApproval.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobDescriptionService {

    private final JobDescriptionRepository jdRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final TextExtractionService extractionService;
    private final NlpService nlpService; // The service that picks out nouns/keywords

    @Transactional
    public UUID createJobDescription(String title, MultipartFile file, String manualText, UUID ownerId) throws IOException {
        User owner = userRepository.findById(ownerId).orElseThrow();

        JobDescription jd = new JobDescription();
        jd.setJobTitle(title);
        jd.setOwner(owner);

        String textToAnalyze = "";

        // If a file is uploaded, use it primarily
        if (file != null && !file.isEmpty()) {
            String filePath = fileStorageService.save(file, "jds");
            jd.setFilePath(filePath);
            textToAnalyze = extractionService.extractText(filePath);
        } else {
            // Fallback to the pasted text
            textToAnalyze = manualText;
        }

        // Still store the text for the "Truth-Machine"
        jd.setExtractedText(textToAnalyze);

        // Extract keywords from whichever source we got text from
        Set<String> keywords = nlpService.extractTechnicalNouns(textToAnalyze);
        jd.setExtractedKeywords(keywords);

        return jdRepository.save(jd).getId();
    }
}