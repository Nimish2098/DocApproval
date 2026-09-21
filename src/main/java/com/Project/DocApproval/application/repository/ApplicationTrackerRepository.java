package com.Project.DocApproval.application.repository;

import com.Project.DocApproval.application.entity.Application;
import com.Project.DocApproval.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationTrackerRepository extends JpaRepository<Application, UUID> {

    Optional<Application> findById(UUID applicationId);

    List<Application> findByUserOrderByLocalDateTimeDesc(User user);

    Optional<Application> findByUuidAndUser(UUID uuid, User user);
}
