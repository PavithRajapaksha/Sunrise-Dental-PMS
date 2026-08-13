package com.sunrise.sunrisedentalpms.model;

import java.util.Objects;


public class User {

    private final String userId;
    private String username;
    private String hashedPassword;
    private UserRole role;

    public User(String userId, String username, String hashedPassword, UserRole role) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        this.userId = userId.trim();
        setUsername(username);
        setHashedPassword(hashedPassword);
        setRole(role);
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().length() < 4) {
            throw new IllegalArgumentException("Username must be at least 4 characters");
        }
        this.username = username.trim();
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }
        this.hashedPassword = hashedPassword;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = Objects.requireNonNull(role, "Role cannot be null");
    }

    @Override
    public String toString() {
        return "User{" + "userId='" + userId + '\'' + ", username='" + username + '\'' + ", role=" + role + '}';
    }
}