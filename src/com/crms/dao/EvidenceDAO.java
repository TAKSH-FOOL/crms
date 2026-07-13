package com.crms.dao;

import com.crms.model.Evidence;
import com.crms.config.DatabaseConnection;
import com.crms.util.AuditLogger;
import com.crms.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvidenceDAO {

    public static boolean create(Evidence evidence) {
        String sql = "INSERT INTO evidence (evidence_number, fir_number, crime_number, type, description, custodian, is_active) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, evidence.getEvidenceNumber());
            ps.setString(2, evidence.getFirNumber());
            ps.setString(3, evidence.getCrimeNumber());
            ps.setString(4, evidence.getType());
            ps.setString(5, evidence.getDescription());
            ps.setString(6, evidence.getCustodian());
            ps.setBoolean(7, evidence.isActive());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) evidence.setId(rs.getInt(1));
            }
            AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                    Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                    "CREATE_EVIDENCE", "evidence", evidence.getEvidenceNumber(), null, evidence.toString());
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create evidence: " + e.getMessage());
            return false;
        }
    }

    public static Evidence getById(int id) {
        String sql = "SELECT * FROM evidence WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapEvidence(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Evidence getByEvidenceNumber(String evidenceNumber) {
        String sql = "SELECT * FROM evidence WHERE evidence_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setString(1, evidenceNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapEvidence(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Evidence> getByFIRNumber(String firNumber) {
        List<Evidence> list = new ArrayList<>();
        String sql = "SELECT * FROM evidence WHERE fir_number = ? AND is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setString(1, firNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEvidence(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Evidence> getByCrimeNumber(String crimeNumber) {
        List<Evidence> list = new ArrayList<>();
        String sql = "SELECT * FROM evidence WHERE crime_number = ? AND is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setString(1, crimeNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEvidence(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(Evidence evidence) {
        String sql = "UPDATE evidence SET fir_number=?, crime_number=?, type=?, description=?, custodian=?, is_active=? " +
                "WHERE evidence_number=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            Evidence old = null;
            String selectOld = "SELECT * FROM evidence WHERE evidence_number = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                 ) {
                psOld.setString(1, evidence.getEvidenceNumber());
                try (ResultSet rsOld = psOld.executeQuery()) {
                    if (rsOld.next()) old = mapEvidence(rsOld);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, evidence.getFirNumber());
                ps.setString(2, evidence.getCrimeNumber());
                ps.setString(3, evidence.getType());
                ps.setString(4, evidence.getDescription());
                ps.setString(5, evidence.getCustodian());
                ps.setBoolean(6, evidence.isActive());
                ps.setString(7, evidence.getEvidenceNumber());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "UPDATE_EVIDENCE", "evidence", evidence.getEvidenceNumber(),
                        old != null ? old.toString() : null, evidence.toString());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update evidence: " + e.getMessage());
            return false;
        }
    }

    public static boolean delete(String evidenceNumber) {
        String sql = "UPDATE evidence SET is_active = false WHERE evidence_number = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            Evidence old = getByEvidenceNumber(evidenceNumber);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, evidenceNumber);
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "DELETE_EVIDENCE", "evidence", evidenceNumber,
                        old != null ? old.toString() : null, "is_active=false");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to delete evidence: " + e.getMessage());
            return false;
        }
    }

    private static Evidence mapEvidence(ResultSet rs) throws SQLException {
        Evidence e = new Evidence();
        e.setId(rs.getInt("id"));
        e.setEvidenceNumber(rs.getString("evidence_number"));
        e.setFirNumber(rs.getString("fir_number"));
        e.setCrimeNumber(rs.getString("crime_number"));
        e.setType(rs.getString("type"));
        e.setDescription(rs.getString("description"));
        e.setCustodian(rs.getString("custodian"));
        e.setActive(rs.getBoolean("is_active"));
        return e;
    }
}