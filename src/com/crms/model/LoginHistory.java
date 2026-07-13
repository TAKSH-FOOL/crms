package com.crms.model;

import java.time.LocalDateTime;

public class LoginHistory {
    private int id;
    private String username;
    private LocalDateTime loginTime;
    private String ipAddress;
    private boolean success;
    private String failureReason;

    public LoginHistory() {}

    public LoginHistory(int id, String username, LocalDateTime loginTime, String ipAddress,
                        boolean success, String failureReason) {
        this.id = id;
        this.username = username;
        this.loginTime = loginTime;
        this.ipAddress = ipAddress;
        this.success = success;
        this.failureReason = failureReason;
    }

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
        return "LoginHistory{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", loginTime=" + loginTime +
                ", success=" + success +
                '}';
    }
}