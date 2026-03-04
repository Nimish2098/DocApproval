package com.Project.DocApproval.controller;

import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.UserRepository;
import com.Project.DocApproval.service.ResumeService;
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
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeServiceImpl;
    private final UserRepository userRepository;

    @PostMapping(value = "/upload/{jdId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> uploadResume(
            @PathVariable UUID jdId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @AuthenticationPrincipal UserDetails userDetails) {


        try {
            User currentUser = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UUID userId = currentUser.getId();
            UUID resumeId = resumeServiceImpl.initiateAnalysis(name, email, file, jdId, userId);

            // 2. Start the background NLP comparison
            // Being @Async, this runs in a separate thread
            resumeServiceImpl.processResume(resumeId);

            // 202 Accepted is the correct status for background tasks
            return ResponseEntity.accepted().body(resumeId);

        } catch (IOException e) {
            // This is caught if the physical file save fails
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}