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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.UUID;

// com/yourpackage/service/AuthService.java
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;  // ← inject

    public String getRefreshTokenForUser(UUID userId) {
        return refreshTokenService.getTokenForUser(userId);
    }

    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
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
        String email = normalizeEmail(request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        String accessToken = jwtService.generateToken(user);
        refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
                accessToken,
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public AuthResponse refreshToken(String token) {
        RefreshToken refreshToken = refreshTokenService.findByToken(token);
        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user);

        return new AuthResponse(
                newAccessToken,
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public void logout(UserDetails userDetails) {
        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        refreshTokenService.deleteByUser(user);
    }

    public void logoutByRefreshToken(String token) {
        refreshTokenService.deleteByToken(token);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
