package com.Project.DocApproval.model;

import com.Project.DocApproval.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
public class User {

        @Id
        private Long id;

        private String username;
        private String email;
        private String password;

        @Enumerated(EnumType.STRING)
        private Role role;

}
