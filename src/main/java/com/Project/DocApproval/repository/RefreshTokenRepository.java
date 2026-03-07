package com.Project.DocApproval.repository;

import com.Project.DocApproval.model.RefreshToken;
import com.Project.DocApproval.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    @Transactional
    void deleteByUser(User user);
}
