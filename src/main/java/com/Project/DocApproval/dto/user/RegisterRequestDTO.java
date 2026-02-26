package com.Project.DocApproval.dto.user;

import com.Project.DocApproval.enums.Role;

public record RegisterRequestDTO (
        String username,
        String email,
        String password,
        Role role)
{}
 /* "username" : "Nimish",
    "email" : "admin@example.com",
    "password":"123456",
    "role" : "ADMIN"
    */