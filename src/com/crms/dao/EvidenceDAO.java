package com.crms.dao;

import com.crms.model.Evidence;
import com.crms.config.DatabaseConnection;
import com.crms.model.User;
import com.crms.Session;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;

public class EvidenceDAO {

    public static boolean create(Evidence evidence) {
        String sql = "INSERT INTO evidence (fir_number, crime_number, type, description, custodian, image_data) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";

        FileInputStream fis = null;
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, evidence.getFirNumber());
            ps.setString(2, evidence.getCrimeNumber());
            ps.setString(3, evidence.getType());
            ps.setString(4, evidence.getDescription());
            ps.setString(5, evidence.getCustodian());

            // Handle image stream – keep open until after executeUpdate
            String imagePath = evidence.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                fis = new FileInputStream(imagePath);
                ps.setBinaryStream(6, fis);
            } else {
                ps.setNull(6, Types.LONGVARBINARY);
            }

            int rows = ps.executeUpdate();
            if (rows == 0) return false;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    evidence.setId(id);
                    // Generate evidence number: EV-<FIR_NUMBER>-<ID>
                    String code = "EV-" + evidence.getFirNumber() + "-" + id;
                    // Update the evidence record with the generated number
                    String updateSql = "UPDATE evidence SET evidence_number = ? WHERE id = ?";
                    try (PreparedStatement pst = conn.prepareStatement(updateSql)) {
                        pst.setString(1, code);
                        pst.setInt(2, id);
                        pst.executeUpdate();
                    }
                    evidence.setEvidenceNumber(code);
                }
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Failed to create evidence: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Failed to read image file: " + e.getMessage());
            return false;
        } finally {
            if (fis != null) {
                try { fis.close(); } catch (IOException ignored) {}
            }
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

    public static boolean update(Evidence evidence) {
        String sql = "UPDATE evidence SET fir_number=?, crime_number=?, type=?, description=?, custodian=?, image_data=?, is_active=? " +
                "WHERE evidence_number=?";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";

        FileInputStream fis = null;
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username)) {
            // Fetch old state (optional – for audit)
            Evidence old = null;
            String selectOld = "SELECT * FROM evidence WHERE evidence_number = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld)) {
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

                // Handle image stream – keep open until after executeUpdate
                String imagePath = evidence.getImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    fis = new FileInputStream(imagePath);
                    ps.setBinaryStream(6, fis);
                } else {
                    ps.setNull(6, Types.LONGVARBINARY);
                }

                ps.setBoolean(7, evidence.isActive());
                ps.setString(8, evidence.getEvidenceNumber());

                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update evidence: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Failed to read image file: " + e.getMessage());
            return false;
        } finally {
            if (fis != null) {
                try { fis.close(); } catch (IOException ignored) {}
            }
        }
    }


    public static LinkedList<Evidence> getAll() {
        LinkedList<Evidence> list = new LinkedList<>();
        String sql = "SELECT * FROM evidence ";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapEvidence(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static LinkedList<Evidence> getEvidenceByStationId(int stationId) {
        LinkedList<Evidence> list = new LinkedList<>();
        String sql = "SELECT e.* FROM evidence e " +
                "JOIN fir f ON e.fir_number = f.fir_number " +
                "WHERE f.station_id = ? AND e.is_active = 1 AND f.is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEvidence(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
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
        Blob blob = rs.getBlob("image_data");
        if (blob != null) {
            e.setImageData(blob.getBytes(1, (int) blob.length()));
        }
        return e;
    }

    public static LinkedList<Evidence> getEvidenceByOfficerId(int officerId) {
        LinkedList<Evidence> list = new LinkedList<>();
        String sql = "SELECT e.* FROM evidence e " +
                "JOIN fir f ON e.fir_number = f.fir_number " +
                "WHERE f.assigned_officer_id = ? AND e.is_active = 1 AND f.is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, officerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEvidence(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void displayEvidenceByOfficerId(int officerId) {
        LinkedList<Evidence> list = getEvidenceByOfficerId(officerId);
        if (list == null || list.isEmpty()) {
            System.out.println("No evidence found for officer ID: " + officerId);
            return;
        }

        System.out.println("\n+------+----------------------+----------------------+----------------------+-----------------+--------+");
        System.out.printf("| %-4s | %-20s | %-20s | %-20s | %-15s | %-6s |\n",
                "ID", "Evidence Number", "FIR Number", "Type", "Custodian", "Active");
        System.out.println("+------+----------------------+----------------------+----------------------+-----------------+--------+");

        for (Evidence e : list) {
            String evNum = e.getEvidenceNumber() != null ? e.getEvidenceNumber() : "N/A";
            String firNum = e.getFirNumber() != null ? e.getFirNumber() : "N/A";
            String type = e.getType() != null ? e.getType() : "N/A";
            String custodian = e.getCustodian() != null ? e.getCustodian() : "N/A";
            String active = e.isActive() ? "Yes" : "No";

            System.out.printf("| %-4d | %-20s | %-20s | %-20s | %-15s | %-6s |\n",
                    e.getId(), evNum, firNum, type, custodian, active);
        }
        System.out.println("+------+----------------------+----------------------+----------------------+-----------------+--------+");
    }


}