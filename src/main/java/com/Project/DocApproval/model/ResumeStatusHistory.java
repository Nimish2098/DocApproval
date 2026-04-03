package com.Project.DocApproval.model;

import com.Project.DocApproval.enums.ResumeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Enumerated(EnumType.STRING)
    private ResumeStatus status;
    private String StatusComment;

    private LocalDateTime localDateTime;
}
