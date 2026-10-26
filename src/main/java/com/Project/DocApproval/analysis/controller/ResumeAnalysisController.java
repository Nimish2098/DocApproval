package com.Project.DocApproval.analysis.controller;

import com.Project.DocApproval.analysis.dto.ResumeAnalysisResponse;
import com.Project.DocApproval.analysis.service.ResumeAnalysisService;
import com.Project.DocApproval.auth.entity.AppUser;
import com.Project.DocApproval.auth.repository.AppUserRepository;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/resume-analysis")
public class ResumeAnalysisController {

  private final ResumeAnalysisService service;
  private final AppUserRepository userRepository;

  public ResumeAnalysisController(
      ResumeAnalysisService service,
      AppUserRepository userRepository) {
    this.service = service;
    this.userRepository = userRepository;
  }

  @PostMapping(
      value = "/analyze",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ResumeAnalysisResponse> analyze(
      @RequestPart("file") MultipartFile file,
      Authentication authentication) throws IOException {

    Long userId = getAuthenticatedUserId(authentication);

    ResumeAnalysisResponse response =
        service.analyze(file, userId);

    return ResponseEntity.ok(response);
  }

  @GetMapping
  public List<ResumeAnalysisResponse> history(
      Authentication authentication) {

    Long userId = requireAuthenticatedUserId(authentication);
    return service.history(userId);
  }

  @GetMapping("/{id}")
  public ResumeAnalysisResponse get(
      @PathVariable Long id,
      Authentication authentication) {

    Long userId = requireAuthenticatedUserId(authentication);
    return service.getById(id, userId);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @PathVariable Long id,
      Authentication authentication) {

    Long userId = requireAuthenticatedUserId(authentication);
    service.delete(id, userId);

    return ResponseEntity.noContent().build();
  }

  private Long getAuthenticatedUserId(
      Authentication authentication) {

    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      return null;
    }

    AppUser user = userRepository
        .findByEmailIgnoreCase(authentication.getName())
        .orElseThrow(() ->
                         new org.springframework.security.authentication
                             .BadCredentialsException(
                             "Authenticated user not found"));

    return user.getId();
  }

  private Long requireAuthenticatedUserId(
      Authentication authentication) {

    Long userId = getAuthenticatedUserId(authentication);

    if (userId == null) {
      throw new org.springframework.security.access
          .AccessDeniedException(
          "Please log in to access your analysis history.");
    }

    return userId;
  }
}