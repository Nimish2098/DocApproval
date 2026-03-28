package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.Document;
import com.Project.DocApproval.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByUserOrderByUpdatedAtDesc(User user);
    boolean existsByUserAndTitle(User user, String title);
}
