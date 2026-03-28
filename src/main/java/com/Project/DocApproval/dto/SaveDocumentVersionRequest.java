package com.Project.DocApproval.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaveDocumentVersionRequest {
    @NotBlank(message = "Commit message is required")
    private String commitMessage;
    
    // Optional, defaults to document limit
    private String content;
}
