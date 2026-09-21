package com.Project.DocApproval.application.dto;

import com.Project.DocApproval.application.enums.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplicationRequest {
    @NotBlank(message = "Position is required")
    private String position;

    @NotBlank(message = "Company is required")
    private String company;

    private ApplicationStatus applicationStatus;
    private String url;
}