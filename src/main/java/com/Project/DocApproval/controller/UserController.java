package com.Project.DocApproval.controller;

import com.Project.DocApproval.dto.ChangePasswordRequest;
import com.Project.DocApproval.dto.UpdateProfileRequest;
import com.Project.DocApproval.dto.UserProfileResponse;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.UserRepository;
import com.Project.DocApproval.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Endpoints for user profile management, password handling, and admin user retrieval")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(summary = "Get User Profile", description = "Retrieves the authenticated user's profile details.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    })
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getLoggedInUser(userDetails);
        return ResponseEntity.ok(new UserProfileResponse(
                user.getId(), user.getName(), user.getEmail()
        ));
    }

    @Operation(summary = "Update User Profile", description = "Updates the authenticated user's name and/or email address.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully updated user profile"),
        @ApiResponse(responseCode = "400", description = "Validation error with provided details"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    })
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getLoggedInUser(userDetails);
        return ResponseEntity.ok(userService.updateProfile(user.getId(), request));
    }

    @Operation(summary = "Change Password", description = "Changes the authenticated user's password.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully updated password"),
        @ApiResponse(responseCode = "400", description = "Invalid old password or validation failure"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    })
    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getLoggedInUser(userDetails);
        userService.changePassword(user.getId(), request);
        return ResponseEntity.ok("Password updated successfully");
    }

    @Operation(summary = "Delete User Profile", description = "Permanently deletes the authenticated user's account and associated data.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Successfully deleted user profile"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    })
    @DeleteMapping("/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getLoggedInUser(userDetails);
        userService.deleteUser(user.getId());
    }

    @Operation(summary = "Get All Users (Admin)", description = "Retrieves a comprehensive list of all registered users in the system. Requires ADMIN privileges.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires Admin role")
    })
    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    private User getLoggedInUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}