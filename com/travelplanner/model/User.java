package com.travelplanner.model;

import java.time.LocalDate;

/**
 * Represents an authenticated user of the Travel Planner application.
 * All trips, expenses, itineraries, and logs are scoped to a userId.
 */
public class User extends BaseEntity {
    private String username;
    private String password;   // plain-text for demo; hash in production
    private String fullName;
    private String email;
    private LocalDate joinDate;

    public User() {}

    public User(int id, String username, String password,
                String fullName, String email, LocalDate joinDate) {
        super(id);
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email    = email;
        this.joinDate = joinDate;
    }

    // ── Getters / Setters ──────────────────────────────────────────────────

    public String getUsername() { return username; }
    public void setUsername(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Username cannot be blank.");
        this.username = v.trim().toLowerCase();
    }

    public String getPassword() { return password; }
    public void setPassword(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Password cannot be blank.");
        this.password = v;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v == null ? "" : v.trim(); }

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v == null ? "" : v.trim(); }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate d) { this.joinDate = d; }

    @Override
    public String toString() {
        return "User[" + getId() + "] " + username + " (" + fullName + ")";
    }
}