package com.Project.DocApproval.auth.controller;

import com.Project.DocApproval.auth.dto.AuthResponse;
import com.Project.DocApproval.auth.dto.LoginRequest;
import com.Project.DocApproval.auth.dto.RegisterRequest;
import com.Project.DocApproval.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token management")
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.refresh-cookie-name}")
    private String refreshCookieName;

    @Value("${jwt.refresh-cookie-secure}")
    private boolean refreshCookieSecure;

    @Value("${jwt.refresh-cookie-same-site}")
    private String refreshCookieSameSite;

    @Value("${jwt.refresh-cookie-domain:}")
    private String refreshCookieDomain;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Operation(summary = "Register User", description = "Registers a new user account with the system.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User successfully registered"),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return withRefreshCookie(authService.register(request));
    }

    @Operation(summary = "Login User", description = "Authenticates a user and returns access and refresh JWT tokens.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
        @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return withRefreshCookie(authService.login(request));
    }

    @Operation(summary = "Refresh Token", description = "Generates a new JWT access token using a valid refresh token.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully generated new tokens"),
        @ApiResponse(responseCode = "403", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "${jwt.refresh-cookie-name}", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        return withRefreshCookie(authService.refreshToken(refreshToken));
    }

    @Operation(summary = "Logout User", description = "Logs out the authenticated user by invalidating their tokens.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully logged out"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid JWT token")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @AuthenticationPrincipal UserDetails userDetails,
            @CookieValue(name = "${jwt.refresh-cookie-name}", required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            authService.logoutByRefreshToken(refreshToken);
        } else if (userDetails != null) {
            authService.logout(userDetails);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredRefreshCookie().toString())
                .body("Logged out successfully");
    }

    private ResponseEntity<AuthResponse> withRefreshCookie(AuthResponse response) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(response).toString())
                .body(response);
    }

    private ResponseCookie refreshCookie(AuthResponse response) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(
                refreshCookieName, authService.getRefreshTokenForUser(response.getUserId()))
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path("/api/v1/auth")
                .maxAge(Duration.ofMillis(refreshExpiration));
        if (!refreshCookieDomain.isBlank()) {
            builder.domain(refreshCookieDomain);
        }
        return builder.build();
    }

    private ResponseCookie expiredRefreshCookie() {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path("/api/v1/auth")
                .maxAge(Duration.ZERO);
        if (!refreshCookieDomain.isBlank()) {
            builder.domain(refreshCookieDomain);
        }
        return builder.build();
    }
}