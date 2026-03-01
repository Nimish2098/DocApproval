package com.Project.DocApproval.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "job_descriptions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(columnDefinition = "TEXT")
    private String extractedText; // The 'Truth-Machine' fuel

    @Column(name = "file_path")
    private String filePath; // Path for the Company 'Receipt'

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extracted_keywords", columnDefinition = "jsonb")
    private Set<String> extractedKeywords; // ["Java", "Spring Boot", "Japanese"]

    @Column(name = "is_active")
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // The recruiter or company admin
}