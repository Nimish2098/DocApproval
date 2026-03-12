package com.Project.DocApproval.service.impl;

import com.Project.DocApproval.dto.LatexDocumentResponse;
import com.Project.DocApproval.dto.LatexDocumentSummary;
import com.Project.DocApproval.dto.SaveLatexRequest;
import com.Project.DocApproval.model.LatexDocument;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.LatexDocumentRepository;
import com.Project.DocApproval.repository.UserRepository;
import com.Project.DocApproval.util.LatexTemplateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LatexService {

        private final LatexDocumentRepository latexRepository;
        private final UserRepository userRepository;


        // ── Save or update source ─────────────────────────────────────
        public LatexDocumentResponse save(SaveLatexRequest request,
                        UserDetails userDetails) {
                User user = getUser(userDetails);

                LatexDocument doc = LatexDocument.builder()
                                .user(user)
                                .title(request.getTitle())
                                .latexSource(request.getLatexSource())
                                .lastCompileSuccess(false)
                                .build();

                return toResponse(latexRepository.save(doc));
        }

        // ── Update existing document ──────────────────────────────────
        public LatexDocumentResponse update(UUID id,
                        SaveLatexRequest request,
                        UserDetails userDetails) {
                LatexDocument doc = getOwnedDoc(id, userDetails);
                doc.setTitle(request.getTitle());
                doc.setLatexSource(request.getLatexSource());
                return toResponse(latexRepository.save(doc));
        }

        // ── Compile LaTeX → PDF ───────────────────────────────────────
        public byte[] compile(UUID id, UserDetails userDetails) {
                LatexDocument doc = getOwnedDoc(id, userDetails);
                log.info("Compiling LaTeX document: {}", doc.getTitle());

                try {
                        String latexSource = doc.getLatexSource();

                        Map<String, Object> resource = new HashMap<>();
                        resource.put("main", true);
                        resource.put("content", latexSource);

                        Map<String, Object> body = new HashMap<>();
                        body.put("compiler", "pdflatex");
                        body.put("resources", List.of(resource));

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);

                        HttpEntity<Map<String, Object>> entity =
                                new HttpEntity<>(body, headers);

                        RestTemplate restTemplate = new RestTemplate();
                        ResponseEntity<byte[]> response = restTemplate.exchange(
                                "https://latex.ytotech.com/builds/sync",
                                HttpMethod.POST,
                                entity,
                                byte[].class
                        );

                        byte[] pdfBytes = response.getBody();

                        doc.setCompiledPdf(pdfBytes);
                        doc.setLastCompileSuccess(true);
                        doc.setLastCompileError(null);
                        latexRepository.save(doc);

                        log.info("Compiled successfully: {}", doc.getTitle());
                        return pdfBytes;

                } catch (Exception e) {
                        doc.setLastCompileSuccess(false);
                        doc.setLastCompileError(e.getMessage());
                        latexRepository.save(doc);
                        log.error("Compile failed: {}", e.getMessage());
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Compilation failed: " + e.getMessage());
                }
        }
        // ── Download last compiled PDF ────────────────────────────────
        public byte[] downloadPdf(UUID id, UserDetails userDetails) {
                LatexDocument doc = getOwnedDoc(id, userDetails);

                if (doc.getCompiledPdf() == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "No compiled PDF. Please compile first.");
                }
                return doc.getCompiledPdf();
        }

        // ── Get all documents (summary) ───────────────────────────────
        public List<LatexDocumentSummary> getAll(UserDetails userDetails) {
                User user = getUser(userDetails);
                return latexRepository.findByUserOrderByUpdatedAtDesc(user)
                                .stream()
                                .map(d -> new LatexDocumentSummary(
                                                d.getId(), d.getTitle(),
                                                d.getLastCompileSuccess(), d.getUpdatedAt()))
                                .toList();
        }

        // ── Get one document with source ──────────────────────────────
        public LatexDocumentResponse getOne(UUID id,
                        UserDetails userDetails) {
                return toResponse(getOwnedDoc(id, userDetails));
        }

        // ── Get starter template ──────────────────────────────────────
        public String getTemplate(UserDetails userDetails) {
                User user = getUser(userDetails);
                return LatexTemplateUtil.resumeTemplate(
                                user.getName(),
                                user.getEmail(),
                                "your-phone-number");
        }

        // ── Delete document ───────────────────────────────────────────
        @Transactional
        public void delete(UUID id, UserDetails userDetails) {
                LatexDocument doc = getOwnedDoc(id, userDetails);
                latexRepository.delete(doc);
        }

        // ── Helpers ───────────────────────────────────────────────────
        private LatexDocument getOwnedDoc(UUID id,
                        UserDetails userDetails) {
                User user = getUser(userDetails);
                LatexDocument doc = latexRepository.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Document not found"));

                if (!doc.getUser().getId().equals(user.getId())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                        "You can only access your own documents");
                }
                return doc;
        }

        private User getUser(UserDetails userDetails) {
                return userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException(
                                                "User not found"));
        }

        private LatexDocumentResponse toResponse(LatexDocument doc) {
                return new LatexDocumentResponse(
                                doc.getId(), doc.getTitle(), doc.getLatexSource(),
                                doc.getLastCompileSuccess(), doc.getLastCompileError(),
                                doc.getCreatedAt(), doc.getUpdatedAt());
        }
}
