package com.Project.DocApproval.auth.repository;

import com.Project.DocApproval.auth.entity.RefreshToken;
import com.Project.DocApproval.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    @Transactional
    void deleteByToken(String token);

    @Transactional
    void deleteByUser(User user);
}
