package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    Optional <Resume> findById(UUID trackingId);
    boolean existsByEmailAndJobDescriptionId(String email, UUID jobId);
}
