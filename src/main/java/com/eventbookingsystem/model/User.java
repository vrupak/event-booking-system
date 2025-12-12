package com.eventbookingsystem.model;

import java.time.LocalDateTime;
import  java.util.UUID;

public class User {
    private UUID userId;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    public User() {}

    public User(UUID userId, String name, String email, LocalDateTime createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    public UUID getUserId() { return userId;}
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getName() { return name;}
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email;}
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
