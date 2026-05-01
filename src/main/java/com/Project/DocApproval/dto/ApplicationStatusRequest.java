package com.Project.DocApproval.dto;

import com.Project.DocApproval.enums.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationStatusRequest {
    private ApplicationStatus status;
}
