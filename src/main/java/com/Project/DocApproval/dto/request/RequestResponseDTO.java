package com.Project.DocApproval.dto.request;

import com.Project.DocApproval.enums.RequestStatus;

import java.time.LocalDateTime;

public class RequestResponseDTO {
    private Long id;
    private String requestType;
    private RequestStatus status;
    private String payload;
    private LocalDateTime createdAt;
}

