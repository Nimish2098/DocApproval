package com.Project.DocApproval.auth.service;

import com.Project.DocApproval.auth.dto.AuthResponse;
import com.Project.DocApproval.auth.dto.LoginRequest;
import com.Project.DocApproval.auth.dto.RegisterRequest;
import com.Project.DocApproval.user.enums.Role;
import com.Project.DocApproval.auth.entity.RefreshToken;
import com.Project.DocApproval.user.entity.User;
import com.Project.DocApproval.user.repository.UserRepository;
import com.Project.DocApproval.auth.security.JwtService;
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

        public String getRefreshTokenForUser(java.util.UUID userId) {
                return refreshTokenService.getTokenForUser(userId);
        }

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
        refreshTokenService.createRefreshToken(saved.getId());

        return new AuthResponse(
                accessToken,
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
        refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
                accessToken,
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    // ── New: refresh access token ─────────────────────────────────────
        public AuthResponse refreshToken(String token) {
                RefreshToken refreshToken = refreshTokenService.findByToken(token);
        refreshTokenService.verifyExpiration(refreshToken);  // throws if expired

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user);

        return new AuthResponse(
                newAccessToken,
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

        public void logoutByRefreshToken(String token) {
                refreshTokenService.deleteByToken(token);
        }
}
