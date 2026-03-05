package com.Project.DocApproval.service.impl;

import com.Project.DocApproval.dto.ChangePasswordRequest;
import com.Project.DocApproval.dto.UpdateProfileRequest;
import com.Project.DocApproval.dto.UserProfileResponse;
import com.Project.DocApproval.exceptions.ResourceNotFoundException;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Update name / email ──────────────────────────────────────────
    public UserProfileResponse updateProfile(UUID id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only update fields that were actually sent
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Check new email isn't already taken by someone else
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        User saved = userRepository.save(user);
        return new UserProfileResponse(saved.getId(), saved.getName(), saved.getEmail());
    }

    // ── Change password ──────────────────────────────────────────────
    public void changePassword(UUID id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Verify current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Current password is incorrect");
        }

        // 2. Confirm new passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "New passwords do not match");
        }

        // 3. Save BCrypt hashed new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ── Delete account ───────────────────────────────────────────────
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    // ── Admin: all users ─────────────────────────────────────────────
    public List<UserProfileResponse> getAllUsers() {

        try {
            return userRepository.findAll()
                    .stream()
                    .map(u -> new UserProfileResponse(u.getId(), u.getName(), u.getEmail()))
                    .toList();
        } catch (Exception e) {
            throw new UsernameNotFoundException("User Not Found"+e);
        }
    }
}