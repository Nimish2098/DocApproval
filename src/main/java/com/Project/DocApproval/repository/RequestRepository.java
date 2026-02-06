package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request,Long> {
    public Request getRequestById(Long id);
}
