package com.Project.DocApproval.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String currentPassword;   // verify old password first
    private String newPassword;
    private String confirmPassword;   // must match newPassword
}