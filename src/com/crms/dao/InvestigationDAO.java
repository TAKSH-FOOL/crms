package com.crms.dao;

import com.crms.model.Investigation;
import com.crms.config.DatabaseConnection;
import com.crms.util.AuditLogger;
import com.crms.Session;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvestigationDAO {

    public static boolean create(Investigation investigation) {
        String sql = "INSERT INTO investigations (fir_number, officer_id, notes, status, start_date, is_active) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, investigation.getFirNumber());
            ps.setInt(2, investigation.getOfficerId());
            ps.setString(3, investigation.getNotes());
            ps.setString(4, investigation.getStatus());
            ps.setTimestamp(5, Timestamp.valueOf(investigation.getStartDate()));
            ps.setBoolean(6, investigation.isActive());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) investigation.setId(rs.getInt(1));
            }
            AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                    Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                    "CREATE_INVESTIGATION", "investigations", investigation.getFirNumber(), null, investigation.toString());
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create investigation: " + e.getMessage());
            return false;
        }
    }

    public static Investigation getByFIRNumber(String firNumber) {
        String sql = "SELECT * FROM investigations WHERE fir_number = ? AND is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setString(1, firNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapInvestigation(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean update(Investigation investigation) {
        String sql = "UPDATE investigations SET officer_id=?, notes=?, status=?, start_date=?, is_active=? " +
                "WHERE fir_number=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            Investigation old = null;
            String selectOld = "SELECT * FROM investigations WHERE fir_number = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                 ) {
                psOld.setString(1, investigation.getFirNumber());
                try (ResultSet rsOld = psOld.executeQuery()) {
                    if (rsOld.next()) old = mapInvestigation(rsOld);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, investigation.getOfficerId());
                ps.setString(2, investigation.getNotes());
                ps.setString(3, investigation.getStatus());
                ps.setTimestamp(4, Timestamp.valueOf(investigation.getStartDate()));
                ps.setBoolean(5, investigation.isActive());
                ps.setString(6, investigation.getFirNumber());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "UPDATE_INVESTIGATION", "investigations", investigation.getFirNumber(),
                        old != null ? old.toString() : null, investigation.toString());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update investigation: " + e.getMessage());
            return false;
        }
    }

    public static boolean delete(String firNumber) {
        String sql = "UPDATE investigations SET is_active = false WHERE fir_number = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            Investigation old = getByFIRNumber(firNumber);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, firNumber);
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "DELETE_INVESTIGATION", "investigations", firNumber,
                        old != null ? old.toString() : null, "is_active=false");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to delete investigation: " + e.getMessage());
            return false;
        }
    }

    private static Investigation mapInvestigation(ResultSet rs) throws SQLException {
        Investigation i = new Investigation();
        i.setId(rs.getInt("id"));
        i.setFirNumber(rs.getString("fir_number"));
        i.setOfficerId(rs.getInt("officer_id"));
        i.setNotes(rs.getString("notes"));
        i.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("start_date");
        if (ts != null) i.setStartDate(ts.toLocalDateTime());
        i.setActive(rs.getBoolean("is_active"));
        return i;
    }
}