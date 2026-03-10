package com.Project.DocApproval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LatexDocumentResponse {
    private UUID id;
    private String title;
    private String latexSource;
    private Boolean lastCompileSuccess;
    private String lastCompileError;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

