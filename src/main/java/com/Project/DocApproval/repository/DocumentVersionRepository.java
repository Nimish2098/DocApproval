package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.Document;
import com.Project.DocApproval.model.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, UUID> {
    List<DocumentVersion> findByDocumentOrderByCreatedAtDesc(Document document);
}
