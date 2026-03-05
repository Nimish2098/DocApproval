package com.Project.DocApproval.service.impl;

import com.Project.DocApproval.model.RefreshToken;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.RefreshTokenRepository;
import com.Project.DocApproval.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

// service/RefreshTokenService.java
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // ── Create and save a new refresh token ──────────────────────────
    public RefreshToken createRefreshToken(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete old refresh token if exists
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())   // random UUID as token
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    // ── Validate refresh token ────────────────────────────────────────
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Refresh token expired. Please login again");
        }
        return token;
    }

    // ── Find by token string ──────────────────────────────────────────
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Invalid refresh token"));
    }

    // ── Delete on logout ──────────────────────────────────────────────
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
