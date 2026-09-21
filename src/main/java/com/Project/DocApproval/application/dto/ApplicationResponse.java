package com.Project.DocApproval.application.dto;

import com.Project.DocApproval.application.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ApplicationResponse {
    private UUID uuid;
    private String position;
    private String company;
    private ApplicationStatus applicationStatus;
    private LocalDateTime localDateTime;
    private String url;
}