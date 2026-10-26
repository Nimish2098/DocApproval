
package com.Project.DocApproval.auth.service;

import com.Project.DocApproval.auth.dto.AuthResponse;
import com.Project.DocApproval.auth.dto.LoginRequest;
import com.Project.DocApproval.auth.dto.RegisterRequest;
import com.Project.DocApproval.auth.entity.AppUser;
import com.Project.DocApproval.auth.repository.AppUserRepository;
import com.Project.DocApproval.auth.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

  private final AppUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final JwtService jwtService;

  public AuthService(
      AppUserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      UserDetailsService userDetailsService,
      JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.jwtService = jwtService;
  }

  @Transactional
  public AuthResponse register(RegisterRequest request) {

    String email = request.email().trim().toLowerCase();

    if (userRepository.existsByEmailIgnoreCase(email)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "Email is already registered"
      );
    }

    AppUser user = new AppUser(
        request.name().trim(),
        email,
        passwordEncoder.encode(request.password())
    );

    user = userRepository.saveAndFlush(user);

    UserDetails userDetails =
        userDetailsService.loadUserByUsername(user.getEmail());

    String token = jwtService.generateToken(userDetails);

    return new AuthResponse(
        token,
        "Bearer",
        user.getId(),
        user.getName(),
        user.getEmail()
    );
  }

  public AuthResponse login(LoginRequest request) {

    String email = request.email().trim().toLowerCase();

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            email,
            request.password()
        )
    );

    UserDetails userDetails =
        userDetailsService.loadUserByUsername(email);

    AppUser user = userRepository
        .findByEmailIgnoreCase(email)
        .orElseThrow();

    String token = jwtService.generateToken(userDetails);

    return new AuthResponse(
        token,
        "Bearer",
        user.getId(),
        user.getName(),
        user.getEmail()
    );
  }
}
