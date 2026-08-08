package com.crms.model;

public class User {
     int id;
     String username;
     String password;
     String role;      // ADMIN, OFFICER, STAFF
     int stationId;
     String fullName;
     boolean active;
     String email;
    public User() {}


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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s : %-40s \n", "ID", id));
        sb.append(String.format("%-20s : %-40s \n", "Username", username != null ? username : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Role", role != null ? role : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Full Name", fullName != null ? fullName : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Station ID", stationId));
        sb.append(String.format("%-20s : %-40s \n", "Active", active ? "✅ Yes" : "❌ No"));
        return sb.toString();
    }
}