package com.Project.DocApproval.service.impl;

import com.Project.DocApproval.dto.AuthResponse;
import com.Project.DocApproval.dto.LoginRequest;
import com.Project.DocApproval.dto.RefreshTokenRequest;
import com.Project.DocApproval.dto.RegisterRequest;
import com.Project.DocApproval.enums.Role;
import com.Project.DocApproval.model.RefreshToken;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.UserRepository;
import com.Project.DocApproval.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// com/yourpackage/service/AuthService.java
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;  // ← inject

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);
        String accessToken = jwtService.generateToken(saved);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(saved.getId()); // ← new

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),   // ← new
                saved.getId(),
                saved.getName(),
                saved.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId()); // ← new

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),   // ← new
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    // ── New: refresh access token ─────────────────────────────────────
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.verifyExpiration(refreshToken);  // throws if expired

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user);

        return new AuthResponse(
                newAccessToken,
                refreshToken.getToken(),   // same refresh token, not rotated
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    // ── New: logout ───────────────────────────────────────────────────
    public void logout(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenService.deleteByUser(user);  // invalidate refresh token
    }
}
