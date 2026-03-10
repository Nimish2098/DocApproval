package com.Project.DocApproval.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaveLatexRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "LaTeX source is required")
    private String latexSource;
}
