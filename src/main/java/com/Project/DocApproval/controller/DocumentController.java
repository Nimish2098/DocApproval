package com.Project.DocApproval.controller;

import com.Project.DocApproval.dto.DocumentResponse;
import com.Project.DocApproval.dto.DocumentSummary;
import com.Project.DocApproval.dto.SaveDocumentRequest;
import com.Project.DocApproval.service.impl.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Management", description = "Endpoints for creating, updating, retrieving, and deleting standard text/markdown documents")
public class DocumentController {

    private final DocumentService documentService;

    @Operation(summary = "Create Document (Plain Text)", description = "Creates a new document using plain text content and a title parameter.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Document successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input or missing required fields"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Valid JWT token required")
    })
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<DocumentResponse> savePlainText(
            @Parameter(description = "Document title") @RequestParam String title,
            @Parameter(description = "Document content") @RequestBody String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        SaveDocumentRequest request = new SaveDocumentRequest();
        request.setTitle(title);
        request.setContent(content);
        return ResponseEntity.ok(documentService.save(request, userDetails));
    }

    @Operation(summary = "Create Document (JSON)", description = "Creates a new document using a JSON payload containing title and content.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Document successfully created"),
        @ApiResponse(responseCode = "400", description = "Validation error in JSON payload"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Valid JWT token required")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentResponse> saveJson(
            @Valid @RequestBody SaveDocumentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(documentService.save(request, userDetails));
    }

    @Operation(summary = "Update Document", description = "Updates an existing document's title and content.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Document successfully updated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the document owner"),
        @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponse> update(
            @Parameter(description = "UUID of the document") @PathVariable UUID id,
            @Valid @RequestBody SaveDocumentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                documentService.update(id, request, userDetails));
    }

    @Operation(summary = "Get All Documents", description = "Retrieves a list of all documents belonging to the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of documents")
    @GetMapping
    public ResponseEntity<List<DocumentSummary>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(documentService.getAll(userDetails));
    }

    @Operation(summary = "Get Document by ID", description = "Retrieves a specific document with its full content.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved document"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the document owner"),
        @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getOne(
            @Parameter(description = "UUID of the document") @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                documentService.getOne(id, userDetails));
    }

    @Operation(summary = "Delete Document", description = "Deletes a specific document based on its internal ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Document successfully deleted"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the document owner"),
        @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "UUID of the document") @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        documentService.delete(id, userDetails);
    }
}
