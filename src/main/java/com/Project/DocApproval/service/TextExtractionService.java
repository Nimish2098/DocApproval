package com.Project.DocApproval.service;

import com.Project.DocApproval.exceptions.AnalysisException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class TextExtractionService {

    // Reuse a single Tika instance for better performance
    private final Tika tika = new Tika();

    public String extractText(String filePath) {
        try {
            File file = new File(filePath);

            // Basic check before passing to Tika
            if (!file.exists()) {
                throw new AnalysisException("File not found at path: " + filePath);
            }

            log.info("Starting text extraction for file: {}", file.getName());
            String extractedText = tika.parseToString(file);

            if (extractedText == null || extractedText.isBlank()) {
                log.warn("Extracted text is empty for file: {}", filePath);
                return "";
            }

            return extractedText;

        } catch (IOException e) {
            log.error("Tika failed to parse file {}: {}", filePath, e.getMessage());
            // Use your custom AnalysisException so the Global Handler can catch it
            throw new AnalysisException("Failed to extract text: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during extraction: {}", e.getMessage());
            throw new AnalysisException("Critical error during text extraction.");
        }
    }
}