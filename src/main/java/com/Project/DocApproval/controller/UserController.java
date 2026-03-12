package com.Project.DocApproval.controller;

import com.Project.DocApproval.dto.ChangePasswordRequest;
import com.Project.DocApproval.dto.UpdateProfileRequest;
import com.Project.DocApproval.dto.UserProfileResponse;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.UserRepository;
import com.Project.DocApproval.service.impl.UserService;
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
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    // ── GET OWN PROFILE ──────────────────────────────────────────────
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getLoggedInUser(userDetails);
        return ResponseEntity.ok(new UserProfileResponse(
                user.getId(), user.getName(), user.getEmail()
        ));
    }

    // ── UPDATE NAME / EMAIL ──────────────────────────────────────────
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getLoggedInUser(userDetails);
        return ResponseEntity.ok(userService.updateProfile(user.getId(), request));
    }

    // ── CHANGE PASSWORD ──────────────────────────────────────────────
    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getLoggedInUser(userDetails);
        userService.changePassword(user.getId(), request);
        return ResponseEntity.ok("Password updated successfully");
    }

    // ── DELETE OWN ACCOUNT ───────────────────────────────────────────
    @DeleteMapping("/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getLoggedInUser(userDetails);
        userService.deleteUser(user.getId());
    }

    // ── ADMIN: GET ALL USERS ─────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ── HELPER ───────────────────────────────────────────────────────
    private User getLoggedInUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}