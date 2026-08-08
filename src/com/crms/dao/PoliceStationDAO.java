package com.crms.dao;

import com.crms.model.PoliceStation;
import com.crms.config.DatabaseConnection;
import com.crms.model.User;
import com.crms.Session;

import java.sql.*;
import java.util.LinkedList;


public class PoliceStationDAO {

    public static boolean create(PoliceStation station) {
        String sql = "INSERT INTO police_stations (station_name, address, phone, is_active,station_code) VALUES (?,?,?,?,?)";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, station.getStationName());
            ps.setString(2, station.getAddress());
            ps.setString(3, station.getPhone());
            ps.setBoolean(4, station.isActive());
            ps.setString(5, station.getStationCode());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) station.setId(rs.getInt(1));
            }

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

    public static LinkedList<PoliceStation> getAllActive() {
        LinkedList<PoliceStation> list = new LinkedList<>();
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
        String sql = "UPDATE police_stations SET station_name=?, address=?, phone=?, is_active=?, station_code=? WHERE id=?";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username)) {
            PoliceStation old = null;
            String selectOld = "SELECT * FROM police_stations WHERE station_code = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                 ) {
                psOld.setString(1, station.getStationCode());
                try (ResultSet rsOld = psOld.executeQuery()) {
                    if (rsOld.next()) old = mapStation(rsOld);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, station.getStationName());
                ps.setString(2, station.getAddress());
                ps.setString(3, station.getPhone());
                ps.setBoolean(4, station.isActive());
                ps.setString(5, station.getStationCode());
                ps.setInt(6, station.getId());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;

                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update police station: " + e.getMessage());
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
        s.setStationCode(rs.getString("station_code"));
        return s;
    }

    public static String getStationCodeById(int stationId) {
        String sql = "SELECT station_code FROM police_stations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String code = rs.getString("station_code");
                    return (code != null && !code.isEmpty()) ? code : "UNKNOWN";
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching station code for ID " + stationId + ": " + e.getMessage());
        }
        return "UNKNOWN";
    }



    /**
     * Retrieves the station name for a given station ID.
     * If the station is not found, returns a default value.
     *
     * @param stationId the police station ID
     * @return the station name, or "Unknown Station" if not found
     */
    public static String getStationNameById(int stationId) {
        String sql = "SELECT station_name FROM police_stations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("station_name");
                    return (name != null && !name.isEmpty()) ? name : "Unknown Station";
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching station name for ID " + stationId + ": " + e.getMessage());
        }
        return "Unknown Station";
    }

    public static boolean isStationNameAvailable(String stationName) {
        String sql = "SELECT COUNT(*) FROM police_stations WHERE station_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stationName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking station name availability: " + e.getMessage());
        }
        return false;
    }

    public static String stationCodeGenerator(String stationName){
        if (stationName.length() > 3){
            String[] temp = stationName.trim().split(" ");
            if (temp.length > 2){
                String code = "";
                for (int i = 0; i <temp.length; i++){
                    code = code + temp[i].toUpperCase().charAt(0);
                }
                return code;
            }
            else {
                return stationName.toUpperCase().substring(0,3);
            }
        }
        else {
            return stationName.toUpperCase();
        }
    }

    public static boolean isStationIdValid(int id){
        String sql = "select * from police_stations where id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking station ID validity: " + e.getMessage());
        }
        return false;
    }
}