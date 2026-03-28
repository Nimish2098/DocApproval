package com.Project.DocApproval.controller;

import com.Project.DocApproval.dto.DocumentVersionResponse;
import com.Project.DocApproval.dto.DocumentVersionSummary;
import com.Project.DocApproval.dto.SaveDocumentVersionRequest;
import com.Project.DocApproval.service.impl.DocumentVersioningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents/{documentId}/versions")
@RequiredArgsConstructor
@Tag(name = "Document Versioning", description = "Git-inspired endpoints for committing, reverting, and viewing changes to documents over time")
public class DocumentVersionController {

    private final DocumentVersioningService versioningService;

    @Operation(summary = "Commit Document Version", description = "Creates a new version snapshot (commit) of the specified document.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Version successfully committed"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the document owner"),
        @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @PostMapping
    public ResponseEntity<DocumentVersionResponse> commit(
            @Parameter(description = "UUID of the document") @PathVariable UUID documentId,
            @Valid @RequestBody SaveDocumentVersionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(versioningService.commit(documentId, request, userDetails));
    }

    @Operation(summary = "Get Version History", description = "Retrieves the timeline/history of all commits for a specific document.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved version timeline"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the document owner"),
        @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @GetMapping
    public ResponseEntity<List<DocumentVersionSummary>> getAllVersions(
            @Parameter(description = "UUID of the document") @PathVariable UUID documentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(versioningService.getAllVersions(documentId, userDetails));
    }

    @Operation(summary = "Get Specific Version", description = "Retrieves the full content snapshot and details of a specific historical version.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved version details"),
        @ApiResponse(responseCode = "400", description = "Version does not belong to the requested document"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the document owner"),
        @ApiResponse(responseCode = "404", description = "Document or Version not found")
    })
    @GetMapping("/{versionId}")
    public ResponseEntity<DocumentVersionResponse> getVersion(
            @Parameter(description = "UUID of the document") @PathVariable UUID documentId,
            @Parameter(description = "UUID of the version commit") @PathVariable UUID versionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(versioningService.getVersion(documentId, versionId, userDetails));
    }

    @Operation(summary = "Revert to Version", description = "Reverts the document's current content to the specified historical snapshot, creating a new reversion commit in the process.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully reverted document and created new commit"),
        @ApiResponse(responseCode = "400", description = "Version does not belong to the requested document"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the document owner"),
        @ApiResponse(responseCode = "404", description = "Document or Version not found")
    })
    @PostMapping("/{versionId}/revert")
    public ResponseEntity<DocumentVersionResponse> revert(
            @Parameter(description = "UUID of the document") @PathVariable UUID documentId,
            @Parameter(description = "UUID of the version commit") @PathVariable UUID versionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(versioningService.revert(documentId, versionId, userDetails));
    }
}
