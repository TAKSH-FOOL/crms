package com.crms.util;

import com.crms.config.DatabaseConnection;
import java.sql.*;

public class LoginTracker {

    public static void recordLogin(String username, String ip, boolean success, String failureReason) {
        String sql = "INSERT INTO login_history (username, ip_address, success, failure_reason) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, ip != null ? ip : "127.0.0.1");
            ps.setBoolean(3, success);
            ps.setString(4, failureReason);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to record login: " + e.getMessage());
        }
    }
}