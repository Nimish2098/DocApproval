package com.Project.DocApproval.analysis.controller;

import com.Project.DocApproval.analysis.dto.AnalysisRequest;
import com.Project.DocApproval.analysis.entity.AnalysisResult;
import com.Project.DocApproval.analysis.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping("/analysis")
    public ResponseEntity<AnalysisResult> analyze(
            @Valid @RequestBody AnalysisRequest request) {
        AnalysisResult result = analysisService.performAnalysis(
                request.getResumeText(),
                request.getJobDescription());
        return ResponseEntity.ok(result);
    }
}
