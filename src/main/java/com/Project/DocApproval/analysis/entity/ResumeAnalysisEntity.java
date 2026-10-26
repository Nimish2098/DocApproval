package com.Project.DocApproval.analysis.entity;

// ResumeAnalysisEntity.java
import java.time.Instant;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.Project.DocApproval.analysis.dto.*;

@Entity
@Table(name = "resume_analysis")
public class ResumeAnalysisEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "client_id", length = 64)
  private String clientId;          // guest identity now

  @Column(name = "user_id")
  private Long userId;              // filled when auth arrives

  @Column(name = "original_filename", nullable = false, length = 255)
  private String originalFilename;

  @Column(name = "content_type", length = 100)
  private String contentType;

  @Column(name = "overall_score", nullable = false)
  private int overallScore;

  @Enumerated(EnumType.STRING)
  @Column(name = "score_label", nullable = false, length = 30)
  private ScoreLabel scoreLabel;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "report", nullable = false, columnDefinition = "jsonb")
  private ResumeReport report;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected ResumeAnalysisEntity() {}

  public ResumeAnalysisEntity(
      Long userId,
      String originalFilename,
      String contentType,
      Integer overallScore,
      ScoreLabel scoreLabel,
      ResumeReport report) {

    this.userId = userId;
    this.clientId = null;
    this.originalFilename = originalFilename;
    this.contentType = contentType;
    this.overallScore = overallScore;
    this.scoreLabel = scoreLabel;
    this.report = report;
  }
  @PrePersist
  void onCreate() { createdAt = Instant.now(); }

  public Long getId() { return id; }
  public String getClientId() { return clientId; }
  public Long getUserId() { return userId; }
  public String getOriginalFilename() { return originalFilename; }
  public String getContentType() { return contentType; }
  public int getOverallScore() { return overallScore; }
  public ScoreLabel getScoreLabel() { return scoreLabel; }
  public ResumeReport getReport() { return report; }
  public Instant getCreatedAt() { return createdAt; }
}