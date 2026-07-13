package com.crms.dao;

import com.crms.model.Criminal;
import com.crms.config.DatabaseConnection;
import com.crms.util.AuditLogger;
import com.crms.Session;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CriminalDAO {

    public static boolean create(Criminal criminal) {
        String sql = "INSERT INTO criminals (first_name, last_name, dob, gender, address, phone, wanted_status, is_active) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, criminal.getFirstName());
            ps.setString(2, criminal.getLastName());
            ps.setDate(3, criminal.getDob() != null ? Date.valueOf(criminal.getDob()) : null);
            ps.setString(4, criminal.getGender());
            ps.setString(5, criminal.getAddress());
            ps.setString(6, criminal.getPhone());
            ps.setString(7, criminal.getWantedStatus());
            ps.setBoolean(8, criminal.isActive());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) criminal.setId(rs.getInt(1));
            }
            AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                    Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                    "CREATE_CRIMINAL", "criminals", String.valueOf(criminal.getId()), null, criminal.toString());
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create criminal: " + e.getMessage());
            return false;
        }
    }

    public static Criminal getById(int id) {
        String sql = "SELECT * FROM criminals WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCriminal(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Criminal> searchByName(String name) {
        List<Criminal> list = new ArrayList<>();
        String sql = "SELECT * FROM criminals WHERE (first_name LIKE ? OR last_name LIKE ?) AND is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            String pattern = "%" + name + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapCriminal(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Criminal> getAllActive() {
        List<Criminal> list = new ArrayList<>();
        String sql = "SELECT * FROM criminals WHERE is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapCriminal(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(Criminal criminal) {
        String sql = "UPDATE criminals SET first_name=?, last_name=?, dob=?, gender=?, address=?, phone=?, " +
                "wanted_status=?, is_active=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            Criminal old = null;
            String selectOld = "SELECT * FROM criminals WHERE id = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                 ) {
                psOld.setInt(1, criminal.getId());
                try (ResultSet rsOld = psOld.executeQuery()) {
                    if (rsOld.next()) old = mapCriminal(rsOld);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, criminal.getFirstName());
                ps.setString(2, criminal.getLastName());
                ps.setDate(3, criminal.getDob() != null ? Date.valueOf(criminal.getDob()) : null);
                ps.setString(4, criminal.getGender());
                ps.setString(5, criminal.getAddress());
                ps.setString(6, criminal.getPhone());
                ps.setString(7, criminal.getWantedStatus());
                ps.setBoolean(8, criminal.isActive());
                ps.setInt(9, criminal.getId());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "UPDATE_CRIMINAL", "criminals", String.valueOf(criminal.getId()),
                        old != null ? old.toString() : null, criminal.toString());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update criminal: " + e.getMessage());
            return false;
        }
    }

    public static boolean delete(int criminalId) {
        String sql = "UPDATE criminals SET is_active = false WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            Criminal old = getById(criminalId);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, criminalId);
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "DELETE_CRIMINAL", "criminals", String.valueOf(criminalId),
                        old != null ? old.toString() : null, "is_active=false");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to delete criminal: " + e.getMessage());
            return false;
        }
    }

    public static boolean linkCriminalToCrime(String crimeNumber, int criminalId, String role) {
        String sql = "INSERT INTO crime_criminal_mapping (crime_number, criminal_id, role) VALUES (?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, crimeNumber);
            ps.setInt(2, criminalId);
            ps.setString(3, role);
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                    Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                    "LINK_CRIMINAL_CRIME", "crime_criminal_mapping", crimeNumber + "-" + criminalId,
                    null, "role=" + role);
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to link criminal to crime: " + e.getMessage());
            return false;
        }
    }

    private static Criminal mapCriminal(ResultSet rs) throws SQLException {
        Criminal c = new Criminal();
        c.setId(rs.getInt("id"));
        c.setFirstName(rs.getString("first_name"));
        c.setLastName(rs.getString("last_name"));
        Date d = rs.getDate("dob");
        if (d != null) c.setDob(d.toLocalDate());
        c.setGender(rs.getString("gender"));
        c.setAddress(rs.getString("address"));
        c.setPhone(rs.getString("phone"));
        c.setWantedStatus(rs.getString("wanted_status"));
        c.setActive(rs.getBoolean("is_active"));
        return c;
    }
}