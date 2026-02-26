package com.Project.DocApproval.dto.user;

import com.Project.DocApproval.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterRequestDTO (
        @Schema(example = "nimish_rao", description = "Unique username for the requester")
        String username,

        @Schema(example = "test@gmail.com")
        String email,

        @Schema(example = "password@123", description = "Should have 8 characters with one special character and one number")
        String password,

        Role role)
{}
 /* "username" : "Nimish",
    "email" : "admin@example.com",
    "password":"123456",
    "role" : "ADMIN"
    */