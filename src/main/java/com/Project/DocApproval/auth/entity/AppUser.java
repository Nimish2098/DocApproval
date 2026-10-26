package com.Project.DocApproval.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "app_users",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_app_users_email",
            columnNames = "email"
        )
    }
)
public class AppUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, length = 255)
  private String email;

  // Stores the BCrypt hash, never the raw password.
  @Column(nullable = false, name = "password_hash")
  private String passwordHash;

  @Column(nullable = false, length = 20)
  private String role = "USER";

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  protected AppUser() {
  }

  public AppUser(String name, String email, String passwordHash) {
    this.name = name;
    this.email = email;
    this.passwordHash = passwordHash;
  }

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public String getRole() {
    return role;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
