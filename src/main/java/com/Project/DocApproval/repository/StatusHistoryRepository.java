package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.ResumeStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusHistoryRepository extends JpaRepository<ResumeStatusHistory,Long> {
}
