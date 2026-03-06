package com.Project.DocApproval.model;

import com.Project.DocApproval.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User implements UserDetails {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;
        private String email;
        private String password;
        @Enumerated(EnumType.STRING)           // ← stores "USER" / "ADMIN" as text in DB
        @Column(nullable = false)
        private Role role = Role.USER;

        private String name;

        @Override
        public String getUsername() {
                return email;           // you're using email as login identity
        }

        @Override
        public String getPassword() {
                return password;        // Spring uses this to verify BCrypt hash
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }

        // For a basic project, just return true for these three:
        @Override public boolean isAccountNonExpired()     { return true; }
        @Override public boolean isAccountNonLocked()      { return true; }
        @Override public boolean isCredentialsNonExpired() { return true; }
        @Override public boolean isEnabled()               { return true; }
}
