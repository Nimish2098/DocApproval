package com.Project.DocApproval.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaveDocumentRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;
}
