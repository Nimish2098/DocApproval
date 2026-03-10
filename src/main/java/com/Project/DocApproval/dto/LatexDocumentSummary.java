package com.Project.DocApproval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LatexDocumentSummary {
    private UUID id;
    private String title;
    private Boolean lastCompileSuccess;
    private LocalDateTime updatedAt;
}