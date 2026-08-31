package com.crms.dao;

import com.crms.model.CrimeRecord;
import com.crms.config.DatabaseConnection;
import com.crms.model.User;
import com.crms.Session;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

public class CrimeDAO {

    public static boolean create(CrimeRecord crime) {

        String sql = "INSERT INTO crime_records (fir_number, crime_name, crime_description, " +
                "crime_location, incident_date, status) VALUES (?,?,?,?,?,?)";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, crime.getFirNumber());
            ps.setString(2, crime.getCrimeName());
            ps.setString(3, crime.getCrimeDescription());
            ps.setString(4, crime.getCrimeLocation());
            ps.setTimestamp(5, Timestamp.valueOf(crime.getIncidentDate()));
            ps.setString(6, crime.getStatus());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    crime.setId(rs.getInt(1));
                    int id = rs.getInt(1);
                    String code = "CN-" +PoliceStationDAO.getStationCodeById(Session.getCurrentUser().getStationId())+"-"+ LocalDate.now().getYear()+"-" + id;
                    String sql1 = "update crime_records set crime_number = ? where id = ?";
                    try (PreparedStatement pst = conn.prepareStatement(sql1);){
                        pst.setString(1,code);
                        pst.setInt(2,id);
                        pst.executeUpdate();
                    }
                }
            }
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

    public static LinkedList<CrimeRecord> getAllByStationId(int stationId) {
        LinkedList<CrimeRecord> list = new LinkedList<>();
        String sql = "SELECT cr.* FROM crime_records cr " +
                "JOIN fir f ON cr.fir_number = f.fir_number " +
                "WHERE f.station_id = ? AND cr.is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCrime(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static LinkedList<CrimeRecord> getAllActive() {
        LinkedList<CrimeRecord> list = new LinkedList();
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

    public static LinkedList<CrimeRecord> getAllCrimeRecords() {
        LinkedList<CrimeRecord> list = new LinkedList();
        String sql = "SELECT * FROM crime_records";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapCrime(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static void displayCrimeRecordTable(LinkedList<CrimeRecord> crimes) {
        if (crimes == null || crimes.isEmpty()) {
            System.out.println("No crime records found.");
            return;
        }

        System.out.println("\n+------+----------------------+----------------------+----------------------+----------+--------+");
        System.out.printf("| %-4s | %-20s | %-20s | %-20s | %-8s | %-6s |\n",
                "ID", "Crime Number", "FIR Number", "Crime Name", "Status", "Active");
        System.out.println("+------+----------------------+----------------------+----------------------+----------+--------+");

        for (CrimeRecord c : crimes) {
            String crimeNum = c.getCrimeNumber() != null ? c.getCrimeNumber() : "N/A";
            String firNum = c.getFirNumber() != null ? c.getFirNumber() : "N/A";
            String name = c.getCrimeName() != null ? c.getCrimeName() : "N/A";
            if (name.length() > 20) name = name.substring(0, 17) + "...";
            String status = c.getStatus() != null ? c.getStatus() : "N/A";
            String active = c.isActive() ? "Yes" : "No";

            System.out.printf("| %-4d | %-20s | %-20s | %-20s | %-8s | %-6s |\n",
                    c.getId(), crimeNum, firNum, name, status, active);
        }
        System.out.println("+------+----------------------+----------------------+----------------------+----------+--------+");
    }

    public static boolean update(CrimeRecord crime) {
        String sql = "UPDATE crime_records SET fir_number=?, crime_name=?, crime_description=?, " +
                "crime_location=?, incident_date=?, status=?, is_active=? WHERE crime_number=?";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username)) {
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
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update crime record: " + e.getMessage());
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

    public static LinkedList<CrimeRecord> getCrimeRecordsByStation(int stationId) {
        LinkedList<CrimeRecord> list = new LinkedList<>();
        String sql = "{CALL GetCrimeRecordsByStation(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, stationId);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCrime(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }



    public static LinkedList<CrimeRecord> getCrimeRecordsByOfficerId(int officerId) {
        LinkedList<CrimeRecord> list = new LinkedList<>();
        String sql = "SELECT cr.* FROM crime_records cr " +
                "JOIN fir f ON cr.fir_number = f.fir_number " +
                "WHERE f.assigned_officer_id = ? AND cr.is_active = 1 AND f.is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, officerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCrime(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isCrimeRecordAssignedToOfficer(String crimeNumber, int officerId) {
        if (crimeNumber == null || crimeNumber.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT 1 FROM crime_records cr " +
                "JOIN fir f ON cr.fir_number = f.fir_number " +
                "WHERE cr.crime_number = ? AND f.assigned_officer_id = ? " +
                "AND cr.is_active = 1 AND f.is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, crimeNumber);
            ps.setInt(2, officerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}