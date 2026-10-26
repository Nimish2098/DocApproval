package com.Project.DocApproval.auth.dto;

public record AuthResponse(
    String accessToken,
    String tokenType,
    Long userId,
    String name,
    String email
) {}
