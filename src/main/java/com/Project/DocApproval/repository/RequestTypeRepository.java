package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestTypeRepository extends JpaRepository<RequestType,Long> {
    public RequestType getRequestTypeById(Long id);

}
