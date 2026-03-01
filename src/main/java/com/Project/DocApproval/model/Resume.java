package com.Project.DocApproval.model;

import com.Project.DocApproval.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "resumes")
@Getter
@Setter
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String candidateName;
    private String email;
    private String filePath;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private Double matchScore;

    @Column(columnDefinition = "TEXT")
    private String analysisFeedback;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "missing_skills", columnDefinition = "jsonb")
    private List<String> missingSkills;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate; // The job seeker

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_description_id", nullable = false)
    private JobDescription jobDescription;
}