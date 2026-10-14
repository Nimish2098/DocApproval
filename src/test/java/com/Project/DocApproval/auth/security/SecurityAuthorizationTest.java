package com.Project.DocApproval.auth.security;

import com.Project.DocApproval.user.controller.UserController;
import com.Project.DocApproval.user.entity.User;
import com.Project.DocApproval.user.enums.Role;
import com.Project.DocApproval.user.repository.UserRepository;
import com.Project.DocApproval.user.service.UserDetailsServiceImpl;
import com.Project.DocApproval.user.service.UserService;
import com.Project.DocApproval.security.CorsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, CorsConfig.class, JwtAuthenticationFilter.class})
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void standardUserCanReadOwnProfile() throws Exception {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("member@example.com")
                .name("Member")
                .role(Role.USER)
                .build();
        when(userRepository.findByEmail("member@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/user/profile").with(user("member@example.com").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void standardUserCannotListAllUsers() throws Exception {
        mockMvc.perform(get("/user").with(user("member@example.com").roles("USER")))
                .andExpect(status().isForbidden());
        verifyNoInteractions(userService);
    }

    @Test
    void administratorCanListAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/user").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void profileRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void malformedBearerTokenIsRejectedAsUnauthorized() throws Exception {
        when(jwtService.extractUsername("not-a-jwt"))
                .thenThrow(new IllegalArgumentException("Malformed token"));

        mockMvc.perform(get("/user/profile")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }
}
