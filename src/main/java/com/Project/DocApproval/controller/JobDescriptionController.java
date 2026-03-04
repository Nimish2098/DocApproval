package com.Project.DocApproval.controller;

import com.Project.DocApproval.exceptions.InvalidJobDescriptionException;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.UserRepository;
import com.Project.DocApproval.service.JobDescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobDescriptionController {

    private final JobDescriptionService jdService;
    private final UserRepository userRepository;
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> postJob(
            @RequestParam("title") String title,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "manualText", required = false) String manualText,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()->  new RuntimeException(("User not found")));
        UUID ownerId = currentUser.getId();
        // 1. Validation Logic
        // Throwing a custom exception here allows the GlobalExceptionHandler to return a 400 Bad Request
        if ((file == null || file.isEmpty()) && (manualText == null || manualText.isBlank())) {
            throw new InvalidJobDescriptionException("You must provide either a PDF file or manual text for the job description.");
        }

        // 2. Service Call
        // This will now throw DuplicateApplicationException if the title/owner pair already exists
        UUID jdId = jdService.createJobDescription(title, file, manualText, ownerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(jdId);
    }
}