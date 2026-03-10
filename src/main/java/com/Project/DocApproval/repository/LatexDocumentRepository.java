package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.LatexDocument;
import com.Project.DocApproval.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

    public interface LatexDocumentRepository
        extends JpaRepository<LatexDocument, UUID> {

    List<LatexDocument> findByUserOrderByUpdatedAtDesc(User user);
    boolean existsByUserAndTitle(User user, String title);
}