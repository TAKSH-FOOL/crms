package com.crms.util;

import com.crms.config.DatabaseConnection;
import java.sql.*;

public class LoginTracker {

    public static void recordLogin(String username, boolean success, String failureReason) {
        String sql = "INSERT INTO login_history (username, success, failure_reason) VALUES ( ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setBoolean(2, success);
            ps.setString(3, failureReason);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to record login: " + e.getMessage());
        }
    }
}