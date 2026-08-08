package com.crms.dao;

import com.crms.model.Admin;
import com.crms.config.DatabaseConnection;
import com.crms.Session;
import com.crms.model.User;

import java.sql.*;

public class AdminDAO {

    public static boolean create(Admin admin) {
        String sql = "INSERT INTO admins (user_id, designation, is_super_admin) VALUES (?, ?,?)";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";

        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, admin.getUserId());
            ps.setString(2, admin.getDesignation());
            if (admin.getDesignation().equals("SUPERADMIN")) {
                ps.setBoolean(3, true);
            }
            else {
                ps.setBoolean(3, false);
            }

            int rows = ps.executeUpdate();
            if (rows == 0) return false;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    admin.setId(rs.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Failed to create admin: " + e.getMessage());
            return false;
        }
    }

    private static Admin mapAdmin(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setId(rs.getInt("id"));
        a.setUserId(rs.getInt("user_id"));
        a.setDesignation(rs.getString("designation"));
        a.setSuperAdmin(rs.getBoolean("is_super_admin"));   // <-- add this
        a.setActive(rs.getBoolean("is_active"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) a.setCreatedAt(ts.toLocalDateTime());
        return a;
    }

    // Optional: get admin by user ID
    public static Admin getByUserId(int userId) {
        String sql = "SELECT * FROM admins WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAdmin(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}