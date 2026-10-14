package com.Project.DocApproval.auth.service;

import com.Project.DocApproval.auth.dto.AuthResponse;
import com.Project.DocApproval.auth.dto.LoginRequest;
import com.Project.DocApproval.auth.dto.RegisterRequest;
import com.Project.DocApproval.auth.entity.RefreshToken;
import com.Project.DocApproval.auth.security.JwtService;
import com.Project.DocApproval.user.entity.User;
import com.Project.DocApproval.user.enums.Role;
import com.Project.DocApproval.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreatesStandardUserAndIssuesTokens() {
        UUID userId = UUID.randomUUID();
        RegisterRequest request = new RegisterRequest();
        request.setName(" Test User ");
        request.setEmail(" Test@Example.com ");
        request.setPassword("password");

        when(userRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(userId);
            assertEquals(Role.USER, user.getRole());
            assertEquals("ROLE_USER", user.getAuthorities().iterator().next().getAuthority());
            return user;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("access-token");

        AuthResponse response = authService.register(request);

        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getName());
        assertEquals(userId, response.getUserId());
        verify(refreshTokenService).createRefreshToken(userId);
    }

    @Test
    void registerRejectsAnAlreadyRegisteredEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("TEST@example.com");
        request.setPassword("password");
        when(userRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginAuthenticatesUsingNormalizedEmailAndReturnsAccessToken() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .name("Test User")
                .password("encoded-password")
                .role(Role.USER)
                .build();
        LoginRequest request = new LoginRequest();
        request.setEmail(" TEST@Example.com ");
        request.setPassword("password");
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("access-token");

        AuthResponse response = authService.login(request);

        assertEquals("access-token", response.getAccessToken());
        assertEquals(userId, response.getUserId());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(refreshTokenService).createRefreshToken(userId);
    }

    @Test
    void refreshIssuesNewAccessTokenForValidRefreshToken() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .name("Test User")
                .role(Role.USER)
                .build();
        RefreshToken token = RefreshToken.builder().token("refresh-token").user(user).build();
        when(refreshTokenService.findByToken("refresh-token")).thenReturn(token);
        when(refreshTokenService.verifyExpiration(token)).thenReturn(token);
        when(jwtService.generateToken(user)).thenReturn("new-access-token");

        AuthResponse response = authService.refreshToken("refresh-token");

        assertEquals("new-access-token", response.getAccessToken());
        assertEquals(userId, response.getUserId());
    }
}
