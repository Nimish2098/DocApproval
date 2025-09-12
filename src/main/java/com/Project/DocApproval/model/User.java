package com.Project.DocApproval.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;

@Data
@Entity
@Getter
@Setter
public class User {

    @Id
    private Long employeeId;
    private String name;

    private String email;

    private String Approval;

    private String roles;

    public User(Long employeeId, String name, String roles) {
        this.employeeId = employeeId;
        this.name = name;
        this.roles = roles;
    }
}
