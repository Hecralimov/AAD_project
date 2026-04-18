package com.ecommerce.analytics_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users") 
public class User {

    @Id
    // Because your IDs are strings (UUIDs) and not 1, 2, 3 numbers, we use GenerationType.UUID
    // This tells Spring to generate a new UUID string if you ever save a brand new user.
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Matches the "email" column exactly
    private String email;

    // Maps the database "password_hash" column to the Java "passwordHash" variable
    @Column(name = "password_hash")
    private String passwordHash;

    // Maps the database "role_type" column to the Java "roleType" variable
    @Column(name = "role_type")
    private String roleType;

    // Matches the "gender" column exactly
    private String gender;

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}