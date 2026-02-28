package com.Project.DocApproval.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.Project.DocApproval.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional <Resume> findById(UUID trackingId);
}
