package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}
