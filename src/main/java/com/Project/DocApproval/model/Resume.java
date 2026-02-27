package com.Project.DocApproval.model;

import com.Project.DocApproval.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "resume")
@Getter
@Setter
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String candidateName;

    @Column(nullable = false)
    private String candidateEmail;

    @Column(name = "file_path" ,nullable=false)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    private ApplicationStatus status = ApplicationStatus.UPLOADED;

    @Column(name = "match_score")
    private Double matchScore; // 0.0 to 100.0

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parsed_data", columnDefinition = "jsonb")
    private Map<String, Object> parsedData; // Skills, experience, etc.

    @Column(name = "honesty_critique", columnDefinition = "TEXT")
    private String honestyCritique; // The "Brutally Honest" feedback text

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    private String targetJobDescription; // Stores the JD used for this check

    @Column(name = "missing_keywords", columnDefinition = "jsonb")
    private List<String> missingKeywords; // Directly store what was missing


}
