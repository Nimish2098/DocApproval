package com.Project.DocApproval.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.Project.DocApproval.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    Optional <Resume> findById(UUID trackingId);
}
