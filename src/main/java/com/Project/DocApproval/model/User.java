package com.Project.DocApproval.model;

import com.Project.DocApproval.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name="app_users")
@Getter
@Setter
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        private String username;
        private String email;

        @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
        private List<JobDescription> jobDescriptions;

        // A user can upload many Resumes (Individual role)
        @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
        private List<Resume> resumes;

}
