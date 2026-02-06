package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    public User getUserById(Long id);
}
