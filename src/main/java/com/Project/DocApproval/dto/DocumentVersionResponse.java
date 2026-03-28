package com.Project.DocApproval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class DocumentVersionResponse {
    private UUID id;
    private String commitHash;
    private String commitMessage;
    private String contentSnapshot;
    private String authorName;
    private LocalDateTime createdAt;
}
