package com.Project.DocApproval.model;

import com.Project.DocApproval.enums.RequestStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(columnDefinition = "json")
    private String payload;

    @ManyToOne
    @JsonIgnore
    private User createdBy;

    private LocalDateTime createdAt;
}

