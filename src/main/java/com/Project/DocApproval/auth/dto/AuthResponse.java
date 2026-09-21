package com.Project.DocApproval.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private UUID userId;
    private String name;
    private String email;
}
