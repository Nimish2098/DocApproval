package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationTrackerRepository extends JpaRepository<Application, UUID> {

    Optional<Application> findById(UUID applicationId);
}
