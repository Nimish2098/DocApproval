package com.Project.DocApproval.model;

import com.Project.DocApproval.enums.Role;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User{  // ← implement UserDetails

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false)
        private String email;

        @Column(nullable = false)
        private String password;  // ← must be BCrypt-hashed

        private String username;

        @Enumerated(EnumType.STRING)
        private Role role = Role.USER;


}
