package com.petconnect.model;

import java.sql.Timestamp;

/**
 * Represents a single row in the "users" table - ADMIN, SHELTER, or
 * ADOPTER, distinguished by the role field. Plain data object (POJO);
 * it holds state, it does not talk to the database.
 */
public class User {

    public enum Role {
        ADMIN, SHELTER, ADOPTER
    }

    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private String phone;
    private Role role;
    private Timestamp createdAt;

    // Constructor #1: for a NEW user, before it's been saved.
    public User(String name, String email, String passwordHash, String phone, Role role) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.role = role;
    }

    // Constructor #2: for rebuilding a User FROM a database row.
    public User(int userId, String name, String email, String passwordHash,
                String phone, Role role, Timestamp createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}
