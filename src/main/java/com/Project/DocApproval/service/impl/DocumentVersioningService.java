package com.Project.DocApproval.service.impl;

import com.Project.DocApproval.dto.DocumentVersionResponse;
import com.Project.DocApproval.dto.DocumentVersionSummary;
import com.Project.DocApproval.dto.SaveDocumentVersionRequest;
import com.Project.DocApproval.model.Document;
import com.Project.DocApproval.model.DocumentVersion;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.DocumentRepository;
import com.Project.DocApproval.repository.DocumentVersionRepository;
import com.Project.DocApproval.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentVersioningService {

    private final DocumentVersionRepository versionRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    @Transactional
    public DocumentVersionResponse commit(UUID documentId, SaveDocumentVersionRequest request, UserDetails userDetails) {
        User user = getUser(userDetails);
        Document doc = getOwnedDoc(documentId, user);

        String snapshot = request.getContent() != null ? request.getContent() : doc.getContent();
        
        // auto-save doc content if diff
        if(request.getContent() != null && !request.getContent().equals(doc.getContent())){
            doc.setContent(request.getContent());
            documentRepository.save(doc);
        }

        DocumentVersion version = DocumentVersion.builder()
                .document(doc)
                .commitHash(UUID.randomUUID().toString().substring(0, 8))
                .commitMessage(request.getCommitMessage())
                .contentSnapshot(snapshot)
                .author(user)
                .build();

        return toResponse(versionRepository.save(version));
    }

    public List<DocumentVersionSummary> getAllVersions(UUID documentId, UserDetails userDetails) {
        User user = getUser(userDetails);
        Document doc = getOwnedDoc(documentId, user);

        return versionRepository.findByDocumentOrderByCreatedAtDesc(doc)
                .stream()
                .map(v -> new DocumentVersionSummary(
                        v.getId(), v.getCommitHash(), v.getCommitMessage(),
                        v.getAuthor().getName(), v.getCreatedAt()))
                .toList();
    }

    public DocumentVersionResponse getVersion(UUID documentId, UUID versionId, UserDetails userDetails) {
        User user = getUser(userDetails);
        Document doc = getOwnedDoc(documentId, user);

        DocumentVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Version not found"));

        if (!version.getDocument().getId().equals(doc.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Version does not belong to this document");
        }

        return toResponse(version);
    }

    @Transactional
    public DocumentVersionResponse revert(UUID documentId, UUID versionId, UserDetails userDetails) {
        User user = getUser(userDetails);
        Document doc = getOwnedDoc(documentId, user);

        DocumentVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Version not found"));

        if (!version.getDocument().getId().equals(doc.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Version does not belong to this document");
        }

        // 1. Update document
        doc.setContent(version.getContentSnapshot());
        documentRepository.save(doc);

        // 2. Create a new commit for this revert
        DocumentVersion revertVersion = DocumentVersion.builder()
                .document(doc)
                .commitHash(UUID.randomUUID().toString().substring(0, 8))
                .commitMessage("Revert to " + version.getCommitHash() + ": " + version.getCommitMessage())
                .contentSnapshot(version.getContentSnapshot())
                .author(user)
                .build();

        return toResponse(versionRepository.save(revertVersion));
    }

    private Document getOwnedDoc(UUID id, User user) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

        if (!doc.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own documents");
        }
        return doc;
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private DocumentVersionResponse toResponse(DocumentVersion version) {
        return new DocumentVersionResponse(
                version.getId(), version.getCommitHash(), version.getCommitMessage(),
                version.getContentSnapshot(), version.getAuthor().getName(), version.getCreatedAt());
    }
}
