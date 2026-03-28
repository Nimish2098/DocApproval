package com.Project.DocApproval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class DocumentSummary {
    private UUID id;
    private String title;
    private LocalDateTime updatedAt;
}
