package com.crms.dao;

import com.crms.model.FIR;
import com.crms.config.DatabaseConnection;
import com.crms.util.AuditLogger;
import com.crms.Session;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FIRDAO {

    public static boolean create(FIR fir) {
        String sql = "INSERT INTO fir (fir_number, complainant_name, complainant_contact, incident_location, " +
                "incident_date, incident_description, crime_category, status, assigned_officer_id, station_id, is_active) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fir.getFirNumber());
            ps.setString(2, fir.getComplainantName());
            ps.setString(3, fir.getComplainantContact());
            ps.setString(4, fir.getIncidentLocation());
            ps.setTimestamp(5, Timestamp.valueOf(fir.getIncidentDate()));
            ps.setString(6, fir.getIncidentDescription());
            ps.setString(7, fir.getCrimeCategory());
            ps.setString(8, fir.getStatus());
            if (fir.getAssignedOfficerId() != null) ps.setInt(9, fir.getAssignedOfficerId());
            else ps.setNull(9, Types.INTEGER);
            ps.setInt(10, fir.getStationId());
            ps.setBoolean(11, fir.isActive());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) fir.setId(rs.getInt(1));
            }
            AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                    Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                    "CREATE_FIR", "fir", fir.getFirNumber(), null, fir.toString());
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create FIR: " + e.getMessage());
            return false;
        }
    }

    public static FIR getById(int id) {
        String sql = "SELECT * FROM fir WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapFIR(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FIR getByFIRNumber(String firNumber) {
        String sql = "SELECT * FROM fir WHERE fir_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setString(1, firNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapFIR(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<FIR> getByOfficerId(int officerId) {
        List<FIR> list = new ArrayList<>();
        String sql = "SELECT * FROM fir WHERE assigned_officer_id = ? AND is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, officerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFIR(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<FIR> search(String status, String complainant, String fromDate, String toDate) {
        return searchByStation(null, status, complainant, fromDate, toDate);
    }

    public static List<FIR> searchByStation(Integer stationId, String status, String complainant, String fromDate, String toDate) {
        List<FIR> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM fir WHERE is_active = true");
        List<Object> params = new ArrayList<>();
        if (stationId != null) {
            sql.append(" AND station_id = ?");
            params.add(stationId);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (complainant != null && !complainant.isEmpty()) {
            sql.append(" AND complainant_name LIKE ?");
            params.add("%" + complainant + "%");
        }
        if (fromDate != null && !fromDate.isEmpty()) {
            Timestamp fromTs = toStartOfDay(fromDate);
            if (fromTs != null) {
                sql.append(" AND incident_date >= ?");
                params.add(fromTs);
            }
        }
        if (toDate != null && !toDate.isEmpty()) {
            Timestamp toTs = toEndOfDay(toDate);
            if (toTs != null) {
                sql.append(" AND incident_date <= ?");
                params.add(toTs);
            }
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i+1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFIR(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Timestamp toStartOfDay(String date) {
        try {
            return Timestamp.valueOf(LocalDate.parse(date).atStartOfDay());
        } catch (Exception e) {
            return null;
        }
    }

    private static Timestamp toEndOfDay(String date) {
        try {
            return Timestamp.valueOf(LocalDate.parse(date).atTime(23, 59, 59));
        } catch (Exception e) {
            return null;
        }
    }

    public static List<FIR> getAllActive() {
        List<FIR> list = new ArrayList<>();
        String sql = "SELECT * FROM fir WHERE is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapFIR(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(FIR fir) {
        String sql = "UPDATE fir SET complainant_name=?, complainant_contact=?, incident_location=?, " +
                "incident_date=?, incident_description=?, crime_category=?, status=?, assigned_officer_id=?, station_id=? " +
                "WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            FIR old = null;
            String selectOld = "SELECT * FROM fir WHERE id = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                 ) {
                psOld.setInt(1, fir.getId());
                try (ResultSet rsOld = psOld.executeQuery()) {
                    if (rsOld.next()) old = mapFIR(rsOld);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, fir.getComplainantName());
                ps.setString(2, fir.getComplainantContact());
                ps.setString(3, fir.getIncidentLocation());
                ps.setTimestamp(4, Timestamp.valueOf(fir.getIncidentDate()));
                ps.setString(5, fir.getIncidentDescription());
                ps.setString(6, fir.getCrimeCategory());
                ps.setString(7, fir.getStatus());
                if (fir.getAssignedOfficerId() != null) ps.setInt(8, fir.getAssignedOfficerId());
                else ps.setNull(8, Types.INTEGER);
                ps.setInt(9, fir.getStationId());
                ps.setInt(10, fir.getId());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "UPDATE_FIR", "fir", fir.getFirNumber(),
                        old != null ? old.toString() : null, fir.toString());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update FIR: " + e.getMessage());
            return false;
        }
    }

    public static boolean delete(String firNumber) {
        String sql = "UPDATE fir SET is_active = false WHERE fir_number = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            FIR old = getByFIRNumber(firNumber);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, firNumber);
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "DELETE_FIR", "fir", firNumber,
                        old != null ? old.toString() : null, "is_active=false");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to delete FIR: " + e.getMessage());
            return false;
        }
    }

    private static FIR mapFIR(ResultSet rs) throws SQLException {
        FIR f = new FIR();
        f.setId(rs.getInt("id"));
        f.setFirNumber(rs.getString("fir_number"));
        f.setComplainantName(rs.getString("complainant_name"));
        f.setComplainantContact(rs.getString("complainant_contact"));
        f.setIncidentLocation(rs.getString("incident_location"));
        Timestamp ts = rs.getTimestamp("incident_date");
        if (ts != null) f.setIncidentDate(ts.toLocalDateTime());
        f.setIncidentDescription(rs.getString("incident_description"));
        f.setCrimeCategory(rs.getString("crime_category"));
        f.setStatus(rs.getString("status"));
        int assignedId = rs.getInt("assigned_officer_id");
        if (!rs.wasNull()) f.setAssignedOfficerId(assignedId);
        f.setStationId(rs.getInt("station_id"));
        f.setActive(rs.getBoolean("is_active"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) f.setCreatedAt(created.toLocalDateTime());
        return f;
    }
}