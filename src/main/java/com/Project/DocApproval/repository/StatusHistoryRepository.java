package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.ResumeStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusHistoryRepository extends JpaRepository<ResumeStatusHistory,Long> {
}
