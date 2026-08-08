package com.crms.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginHistory {
     int id;
     String username;
     LocalDateTime loginTime;
     String ipAddress;
     boolean success;
     String failureReason;

    public LoginHistory() {}


    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }


    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("┌──────────────────────────────────────────────────────────┐\n");
        sb.append("│                   LOGIN HISTORY ENTRY                    │\n");
        sb.append("├──────────────────────────────────────────────────────────┤\n");
        sb.append(String.format("%-20s : %-40s \n", "ID", id));
        sb.append(String.format("%-20s : %-40s \n", "Username", username != null ? username : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Login Time",
                loginTime != null ? loginTime.format(formatter) : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "IP Address", ipAddress != null ? ipAddress : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Success", success ? "✅ Yes" : "❌ No"));
        if (!success && failureReason != null) {
            sb.append(String.format("%-20s : %-40s \n", "Failure Reason", failureReason));
        }
        sb.append("└──────────────────────────────────────────────────────────┘");
        return sb.toString();
    }
}