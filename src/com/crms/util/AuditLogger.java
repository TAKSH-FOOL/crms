package com.crms.util;

import com.crms.config.DatabaseConnection;
import java.sql.*;

public class AuditLogger {

    /**
     * Logs an auditable action.
     * @param userId    ID of the user performing the action (0 for system)
     * @param username  username of the user
     * @param action    description of action (e.g., "UPDATE_FIR_STATUS")
     * @param tableName table affected
     * @param recordId  identifier of the record (e.g., FIR number)
     * @param oldValue  previous value (can be null)
     * @param newValue  new value (can be null)
     */
    public static void log(int userId, String username, String action, String tableName,
                           String recordId, String oldValue, String newValue) {
        String sql = "INSERT INTO audit_logs (user_id, username, action, table_name, record_id, old_value, new_value) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, username);
            ps.setString(3, action);
            ps.setString(4, tableName);
            ps.setString(5, recordId);
            ps.setString(6, oldValue);
            ps.setString(7, newValue);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to log audit: " + e.getMessage());
        }
    }
}