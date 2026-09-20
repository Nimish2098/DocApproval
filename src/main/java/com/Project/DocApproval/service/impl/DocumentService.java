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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;

	@Transactional
	public DocumentResponse save(SaveDocumentRequest request, UserDetails userDetails) {
		User user = getUser(userDetails);
		Document document = Document.builder()
				.user(user)
				.title(request.getTitle())
				.content(request.getContent())
				.build();

		return toResponse(documentRepository.save(document));
	}

	@Transactional
	public DocumentResponse update(UUID id, SaveDocumentRequest request, UserDetails userDetails) {
		User user = getUser(userDetails);
		Document document = getOwnedDocument(id, user);
		document.setTitle(request.getTitle());
		document.setContent(request.getContent());

		return toResponse(documentRepository.save(document));
	}

	public List<DocumentSummary> getAll(UserDetails userDetails) {
		User user = getUser(userDetails);
		return documentRepository.findByUserOrderByUpdatedAtDesc(user)
				.stream()
				.map(document -> new DocumentSummary(
						document.getId(), document.getTitle(), document.getCreatedAt()))
				.toList();
	}

	public DocumentResponse getOne(UUID id, UserDetails userDetails) {
		return toResponse(getOwnedDocument(id, getUser(userDetails)));
	}

	@Transactional
	public void delete(UUID id, UserDetails userDetails) {
		Document document = getOwnedDocument(id, getUser(userDetails));
		documentRepository.delete(document);
	}

	private User getUser(UserDetails userDetails) {
		return userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.UNAUTHORIZED, "User not found"));
	}

	private Document getOwnedDocument(UUID id, User user) {
		Document document = documentRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Document not found"));

		if (!document.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(
					HttpStatus.FORBIDDEN, "You can only access your own documents");
		}

		return document;
	}

	private DocumentResponse toResponse(Document document) {
		return new DocumentResponse(
				document.getId(),
				document.getTitle(),
				document.getContent(),
				document.getCreatedAt(),
				document.getUpdatedAt());
	}
}