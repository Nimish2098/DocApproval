package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
