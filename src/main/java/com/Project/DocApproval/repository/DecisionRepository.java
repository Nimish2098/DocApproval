package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.Decision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisionRepository extends JpaRepository<Decision,Long> {
    public Decision getDecisionById(Long id);
}
