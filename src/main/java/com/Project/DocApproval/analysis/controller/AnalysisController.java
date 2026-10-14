package com.Project.DocApproval.analysis.controller;

import com.Project.DocApproval.analysis.dto.AnalysisRequest;
import com.Project.DocApproval.analysis.entity.AnalysisResult;
import com.Project.DocApproval.analysis.service.AnalysisService;
import com.Project.DocApproval.parser.service.TextExtractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;
    private final TextExtractionService extractionService;
    @PostMapping("/analysis")
    public ResponseEntity<String> analyze(
            @RequestParam("file")MultipartFile file) {

        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("Please upload a file");
        }
        try{
            String text = extractionService.extractText(file);
            return ResponseEntity.ok(text);
        } catch (IOException |TikaException e) {
            return ResponseEntity.internalServerError().body("Failed to parse file"+e.getMessage());
        }
    }
}
