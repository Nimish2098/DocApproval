package com.Project.DocApproval.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

// model/LatexDocument.java
@Entity
@Table(name = "latex_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LatexDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String latexSource;


    @Column(columnDefinition = "BYTEA")
    private byte[] compiledPdf;

    private Boolean lastCompileSuccess = false;

    @Column(columnDefinition = "TEXT")
    private String lastCompileError;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
