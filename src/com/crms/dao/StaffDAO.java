package com.crms.dao;

import com.crms.model.Staff;
import com.crms.config.DatabaseConnection;
import com.crms.util.AuditLogger;
import com.crms.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    public static boolean create(Staff staff) {
        String sql = "INSERT INTO staff (user_id, employee_id) VALUES (?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, staff.getUserId());
            ps.setString(2, staff.getEmployeeId());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) staff.setId(rs.getInt(1));
            }
            AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                    Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                    "CREATE_STAFF", "staff", staff.getEmployeeId(), null, staff.toString());
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create staff: " + e.getMessage());
            return false;
        }
    }

    public static Staff getById(int id) {
        String sql = "SELECT * FROM staff WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStaff(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Staff getByUserId(int userId) {
        String sql = "SELECT * FROM staff WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStaff(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Staff> getAll() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staff";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapStaff(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(Staff staff) {
        String sql = "UPDATE staff SET user_id=?, employee_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            Staff old = null;
            String selectOld = "SELECT * FROM staff WHERE id = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                 ) {
                psOld.setInt(1, staff.getId());
                try (ResultSet rsOld = psOld.executeQuery()) {
                    if (rsOld.next()) old = mapStaff(rsOld);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, staff.getUserId());
                ps.setString(2, staff.getEmployeeId());
                ps.setInt(3, staff.getId());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "UPDATE_STAFF", "staff", String.valueOf(staff.getId()),
                        old != null ? old.toString() : null, staff.toString());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update staff: " + e.getMessage());
            return false;
        }
    }

    // No soft delete; handled via user deactivation
    public static boolean delete(int staffId) {
        return false;
    }

    private static Staff mapStaff(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        s.setId(rs.getInt("id"));
        s.setUserId(rs.getInt("user_id"));
        s.setEmployeeId(rs.getString("employee_id"));
        return s;
    }
}