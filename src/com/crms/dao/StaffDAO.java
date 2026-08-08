package com.crms.dao;

import com.crms.model.Staff;
import com.crms.config.DatabaseConnection;
import com.crms.model.User;
import com.crms.util.AuditLogger;
import com.crms.Session;

import java.sql.*;
import java.time.LocalDate;


public class StaffDAO {

    public static boolean create(Staff staff) {
        String sql = "INSERT INTO staff (user_id, station_id) VALUES (?,?)";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, staff.getUserId());
            ps.setInt(2, staff.getStationId());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    staff.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create staff: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateEmployeeIdAndEmail(Staff staff) {
        int year = LocalDate.now().getYear();
        String employeeId = year + String.format("%05d", staff.getId());
        String email = "STAFF" + employeeId + "@police.gov.in";

        String sqlEmp = "UPDATE staff SET employee_id = ? WHERE id = ?";
        String sqlEmail = "UPDATE users SET email = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlEmp);
                 PreparedStatement ps2 = conn.prepareStatement(sqlEmail)) {
                ps1.setString(1, employeeId);
                ps1.setInt(2, staff.getId());
                ps1.executeUpdate();

                ps2.setString(1, email);
                ps2.setInt(2, staff.getUserId());
                ps2.executeUpdate();

                conn.commit();
                staff.setEmployeeId(employeeId);
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    public static boolean update(Staff staff) {
        String sql = "UPDATE staff SET user_id=?, employee_id=? WHERE id=?";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username)) {
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

                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update staff: " + e.getMessage());
            return false;
        }
    }



    private static Staff mapStaff(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        s.setId(rs.getInt("id"));
        s.setUserId(rs.getInt("user_id"));
        s.setEmployeeId(rs.getString("employee_id"));
        return s;
    }
}