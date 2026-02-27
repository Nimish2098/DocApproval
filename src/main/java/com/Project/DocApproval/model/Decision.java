package com.Project.DocApproval.model;

import com.Project.DocApproval.enums.DecisionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "decisions")
@Setter
@Getter
public class Decision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Request request;

    @ManyToOne
    @JsonIgnore
    private User reviewer;

    @Enumerated(EnumType.STRING)
    private DecisionType decision;

    private String comment;

    private LocalDateTime decisionAt;
}

