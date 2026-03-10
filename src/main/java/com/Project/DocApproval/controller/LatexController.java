package com.Project.DocApproval.controller;

import com.Project.DocApproval.dto.LatexDocumentResponse;
import com.Project.DocApproval.dto.LatexDocumentSummary;
import com.Project.DocApproval.dto.SaveLatexRequest;
import com.Project.DocApproval.service.impl.LatexService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/latex")
@RequiredArgsConstructor
public class LatexController {

    private final LatexService latexService;

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<LatexDocumentResponse> save(
            @RequestParam String title,
            @RequestBody String latexSource,
            @AuthenticationPrincipal UserDetails userDetails) {

        SaveLatexRequest request = new SaveLatexRequest();
        request.setTitle(title);
        request.setLatexSource(latexSource);

        return ResponseEntity.ok(latexService.save(request, userDetails));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LatexDocumentResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody SaveLatexRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                latexService.update(id, request, userDetails));
    }

    @PostMapping("/{id}/compile")
    public ResponseEntity<byte[]> compile(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        byte[] pdf = latexService.compile(id, userDetails);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=resume.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        byte[] pdf = latexService.downloadPdf(id, userDetails);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=resume.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping
    public ResponseEntity<List<LatexDocumentSummary>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(latexService.getAll(userDetails));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LatexDocumentResponse> getOne(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                latexService.getOne(id, userDetails));
    }

    @GetMapping("/template")
    public ResponseEntity<String> getTemplate(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(latexService.getTemplate(userDetails));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        latexService.delete(id, userDetails);
    }
}