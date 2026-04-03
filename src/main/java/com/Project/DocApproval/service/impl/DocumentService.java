package com.Project.DocApproval.service.impl;

import com.Project.DocApproval.dto.DocumentResponse;
import com.Project.DocApproval.dto.DocumentSummary;
import com.Project.DocApproval.dto.SaveDocumentRequest;
import com.Project.DocApproval.model.Document;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.DocumentRepository;
import com.Project.DocApproval.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class    DocumentService {

        private final DocumentRepository documentRepository;
        private final UserRepository userRepository;

        public DocumentResponse save(SaveDocumentRequest request,
                        UserDetails userDetails) {
                User user = getUser(userDetails);

                Document doc = Document.builder()
                                .user(user)
                                .title(request.getTitle())
                                .content(request.getContent())
                                .build();

                return toResponse(documentRepository.save(doc));
        }

        public DocumentResponse update(UUID id,
                        SaveDocumentRequest request,
                        UserDetails userDetails) {
                Document doc = getOwnedDoc(id, userDetails);
                doc.setTitle(request.getTitle());
                doc.setContent(request.getContent());
                return toResponse(documentRepository.save(doc));
        }

        public List<DocumentSummary> getAll(UserDetails userDetails) {
                User user = getUser(userDetails);
                return documentRepository.findByUserOrderByUpdatedAtDesc(user)
                                .stream()
                                .map(d -> new DocumentSummary(
                                                d.getId(), d.getTitle(),
                                                d.getUpdatedAt()))
                                .toList();
        }

        public DocumentResponse getOne(UUID id,
                        UserDetails userDetails) {
                return toResponse(getOwnedDoc(id, userDetails));
        }

        @Transactional
        public void delete(UUID id, UserDetails userDetails) {
                Document doc = getOwnedDoc(id, userDetails);
                documentRepository.delete(doc);
        }

        private Document getOwnedDoc(UUID id,
                        UserDetails userDetails) {
                User user = getUser(userDetails);
                Document doc = documentRepository.findById(id)
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

        private DocumentResponse toResponse(Document doc) {
                return new DocumentResponse(
                                doc.getId(), doc.getTitle(), doc.getContent(),
                                doc.getCreatedAt(), doc.getUpdatedAt());
        }
}
