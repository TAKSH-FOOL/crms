package com.crms.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;      // ADMIN, OFFICER, STAFF
    private int stationId;
    private String fullName;
    private boolean active;

    public User() {}

    public User(int id, String username, String password, String role, int stationId, String fullName, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.stationId = stationId;
        this.fullName = fullName;
        this.active = active;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getStationId() { return stationId; }
    public void setStationId(int stationId) { this.stationId = stationId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", stationId=" + stationId +
                ", fullName='" + fullName + '\'' +
                ", active=" + active +
                '}';
    }
}