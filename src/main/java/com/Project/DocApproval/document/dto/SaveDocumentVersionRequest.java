package com.Project.DocApproval.document.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaveDocumentVersionRequest {
    @NotBlank(message = "Commit message is required")
    private String message;
}
