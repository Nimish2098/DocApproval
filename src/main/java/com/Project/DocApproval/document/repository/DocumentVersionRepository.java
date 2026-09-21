package com.Project.DocApproval.document.repository;

import com.Project.DocApproval.document.entity.Document;
import com.Project.DocApproval.document.entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, UUID> {
    List<DocumentVersion> findByDocumentOrderByCreatedAtDesc(Document document);
}
