package com.Project.DocApproval.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class TextExtractionService {

    public String extractText(String filePath) {
        try {
            Tika tika = new Tika();
            File file = new File(filePath);
            return tika.parseToString(file);
        } catch (Exception e) {
            // Rethrow as a custom runtime exception for our Global Handler
            throw new RuntimeException("Failed to extract text from the document: " + e.getMessage());
        }
    }
}