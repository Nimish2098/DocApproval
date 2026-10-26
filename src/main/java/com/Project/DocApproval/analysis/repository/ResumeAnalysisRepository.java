package com.Project.DocApproval.analysis.repository;

import com.Project.DocApproval.analysis.entity.ResumeAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysisEntity, Long>
{
  List<ResumeAnalysisEntity> findAllByClientIdOrderByCreatedAtDesc(String clientId);
  Optional<ResumeAnalysisEntity> findByIdAndClientId(Long id, String clientId);
  List<ResumeAnalysisEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId);
  Optional<ResumeAnalysisEntity> findByIdAndUserId(Long id,Long userId);
}