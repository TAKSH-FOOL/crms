package com.crms.dao;

import com.crms.model.PoliceStation;
import com.crms.config.DatabaseConnection;
import com.crms.util.AuditLogger;
import com.crms.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PoliceStationDAO {

    public static boolean create(PoliceStation station) {
        String sql = "INSERT INTO police_stations (station_name, address, phone, is_active) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, station.getStationName());
            ps.setString(2, station.getAddress());
            ps.setString(3, station.getPhone());
            ps.setBoolean(4, station.isActive());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) station.setId(rs.getInt(1));
            }
            AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                    Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                    "CREATE_STATION", "police_stations", station.getStationName(), null, station.toString());
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create police station: " + e.getMessage());
            return false;
        }
    }

    public static PoliceStation getById(int id) {
        String sql = "SELECT * FROM police_stations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStation(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<PoliceStation> getAllActive() {
        List<PoliceStation> list = new ArrayList<>();
        String sql = "SELECT * FROM police_stations WHERE is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapStation(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(PoliceStation station) {
        String sql = "UPDATE police_stations SET station_name=?, address=?, phone=?, is_active=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            PoliceStation old = null;
            String selectOld = "SELECT * FROM police_stations WHERE id = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                 ) {
                psOld.setInt(1, station.getId());
                try (ResultSet rsOld = psOld.executeQuery()) {
                    if (rsOld.next()) old = mapStation(rsOld);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, station.getStationName());
                ps.setString(2, station.getAddress());
                ps.setString(3, station.getPhone());
                ps.setBoolean(4, station.isActive());
                ps.setInt(5, station.getId());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "UPDATE_STATION", "police_stations", String.valueOf(station.getId()),
                        old != null ? old.toString() : null, station.toString());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update police station: " + e.getMessage());
            return false;
        }
    }

    public static boolean delete(int stationId) {
        String sql = "UPDATE police_stations SET is_active = false WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            PoliceStation old = getById(stationId);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, stationId);
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "DELETE_STATION", "police_stations", String.valueOf(stationId),
                        old != null ? old.toString() : null, "is_active=false");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to delete police station: " + e.getMessage());
            return false;
        }
    }

    private static PoliceStation mapStation(ResultSet rs) throws SQLException {
        PoliceStation s = new PoliceStation();
        s.setId(rs.getInt("id"));
        s.setStationName(rs.getString("station_name"));
        s.setAddress(rs.getString("address"));
        s.setPhone(rs.getString("phone"));
        s.setActive(rs.getBoolean("is_active"));
        return s;
    }
}