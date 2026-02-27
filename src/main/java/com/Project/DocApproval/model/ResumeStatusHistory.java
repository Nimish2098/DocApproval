package com.Project.DocApproval.model;

import com.Project.DocApproval.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume_status_history")
@Getter
@Setter
public class ResumeStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private String comment; // e.g., "AI detected 40% match score"

    @CreationTimestamp
    private LocalDateTime changedAt;
}