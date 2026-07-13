package com.crms.dao;

import com.crms.model.CrimeRecord;
import com.crms.config.DatabaseConnection;
import com.crms.util.AuditLogger;
import com.crms.Session;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CrimeDAO {

    public static boolean create(CrimeRecord crime) {
        String sql = "INSERT INTO crime_records (crime_number, fir_number, crime_name, crime_description, " +
                "crime_location, incident_date, status, is_active) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, crime.getCrimeNumber());
            ps.setString(2, crime.getFirNumber());
            ps.setString(3, crime.getCrimeName());
            ps.setString(4, crime.getCrimeDescription());
            ps.setString(5, crime.getCrimeLocation());
            ps.setTimestamp(6, Timestamp.valueOf(crime.getIncidentDate()));
            ps.setString(7, crime.getStatus());
            ps.setBoolean(8, crime.isActive());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) crime.setId(rs.getInt(1));
            }
            AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                    Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                    "CREATE_CRIME", "crime_records", crime.getCrimeNumber(), null, crime.toString());
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create crime record: " + e.getMessage());
            return false;
        }
    }

    public static CrimeRecord getById(int id) {
        String sql = "SELECT * FROM crime_records WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCrime(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CrimeRecord getByCrimeNumber(String crimeNumber) {
        String sql = "SELECT * FROM crime_records WHERE crime_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setString(1, crimeNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCrime(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<CrimeRecord> getByFIRNumber(String firNumber) {
        List<CrimeRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM crime_records WHERE fir_number = ? AND is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setString(1, firNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapCrime(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<CrimeRecord> getAllActive() {
        List<CrimeRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM crime_records WHERE is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapCrime(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(CrimeRecord crime) {
        String sql = "UPDATE crime_records SET fir_number=?, crime_name=?, crime_description=?, " +
                "crime_location=?, incident_date=?, status=?, is_active=? WHERE crime_number=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            CrimeRecord old = null;
            String selectOld = "SELECT * FROM crime_records WHERE crime_number = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                 ) {
                psOld.setString(1, crime.getCrimeNumber());
                try (ResultSet rsOld = psOld.executeQuery()) {
                    if (rsOld.next()) old = mapCrime(rsOld);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, crime.getFirNumber());
                ps.setString(2, crime.getCrimeName());
                ps.setString(3, crime.getCrimeDescription());
                ps.setString(4, crime.getCrimeLocation());
                ps.setTimestamp(5, Timestamp.valueOf(crime.getIncidentDate()));
                ps.setString(6, crime.getStatus());
                ps.setBoolean(7, crime.isActive());
                ps.setString(8, crime.getCrimeNumber());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "UPDATE_CRIME", "crime_records", crime.getCrimeNumber(),
                        old != null ? old.toString() : null, crime.toString());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update crime record: " + e.getMessage());
            return false;
        }
    }

    public static boolean delete(String crimeNumber) {
        String sql = "UPDATE crime_records SET is_active = false WHERE crime_number = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            CrimeRecord old = getByCrimeNumber(crimeNumber);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, crimeNumber);
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "DELETE_CRIME", "crime_records", crimeNumber,
                        old != null ? old.toString() : null, "is_active=false");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to delete crime record: " + e.getMessage());
            return false;
        }
    }

    private static CrimeRecord mapCrime(ResultSet rs) throws SQLException {
        CrimeRecord c = new CrimeRecord();
        c.setId(rs.getInt("id"));
        c.setCrimeNumber(rs.getString("crime_number"));
        c.setFirNumber(rs.getString("fir_number"));
        c.setCrimeName(rs.getString("crime_name"));
        c.setCrimeDescription(rs.getString("crime_description"));
        c.setCrimeLocation(rs.getString("crime_location"));
        Timestamp ts = rs.getTimestamp("incident_date");
        if (ts != null) c.setIncidentDate(ts.toLocalDateTime());
        c.setStatus(rs.getString("status"));
        c.setActive(rs.getBoolean("is_active"));
        return c;
    }
}