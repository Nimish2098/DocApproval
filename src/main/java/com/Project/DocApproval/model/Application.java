package com.Project.DocApproval.model;

import com.Project.DocApproval.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Data
@Builder
@Table(name = "application_tracker  ")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID uuid;
    private String position;
    private String company;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;

    private LocalDateTime localDateTime;
    private String url;


}
/*Position	Company	Status	Date	Link*/