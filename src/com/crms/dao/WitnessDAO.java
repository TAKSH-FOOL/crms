package com.crms.dao;

import com.crms.model.User;
import com.crms.model.Witness;
import com.crms.config.DatabaseConnection;
import com.crms.Session;

import java.sql.*;
import java.util.LinkedList;


public class WitnessDAO {

    public static boolean create(Witness witness) {
        String sql = "INSERT INTO witnesses (fir_number, first_name, last_name, contact, statement) VALUES (?,?,?,?,?)";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, witness.getFirNumber());
            ps.setString(2, witness.getFirstName());
            ps.setString(3, witness.getLastName());
            ps.setString(4, witness.getContact());
            ps.setString(5, witness.getStatement());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) witness.setId(rs.getInt(1));
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create witness: " + e.getMessage());
            return false;
        }
    }

    public static Witness getById(int id) {
        String sql = "SELECT * FROM witnesses WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapWitness(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean update(Witness witness) {
        String sql = "UPDATE witnesses SET fir_number=?, first_name=?, last_name=?, contact=?, statement=? WHERE id=?";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username)) {
            Witness old = null;
            String selectOld = "SELECT * FROM witnesses WHERE id = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                 ) {
                psOld.setInt(1, witness.getId());
                try (ResultSet rsOld = psOld.executeQuery()) {
                    if (rsOld.next()) old = mapWitness(rsOld);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, witness.getFirNumber());
                ps.setString(2, witness.getFirstName());
                ps.setString(3, witness.getLastName());
                ps.setString(4, witness.getContact());
                ps.setString(5, witness.getStatement());
                ps.setInt(6, witness.getId());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;

                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update witness: " + e.getMessage());
            return false;
        }
    }

    public static LinkedList<Witness> getAllActive() {
        LinkedList<Witness> list = new LinkedList<>();
        String sql = "SELECT * FROM witnesses WHERE is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapWitness(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Witness mapWitness(ResultSet rs) throws SQLException {
        Witness w = new Witness();
        w.setId(rs.getInt("id"));
        w.setFirNumber(rs.getString("fir_number"));
        w.setFirstName(rs.getString("first_name"));
        w.setLastName(rs.getString("last_name"));
        w.setContact(rs.getString("contact"));
        w.setStatement(rs.getString("statement"));
        w.setActive(rs.getBoolean("is_active"));
        return w;
    }

    public static LinkedList<Witness> getWitnessesByStationId(int stationId) {
        LinkedList<Witness> list = new LinkedList<>();
        String sql = "SELECT w.* FROM witnesses w " +
                "JOIN fir f ON w.fir_number = f.fir_number " +
                "WHERE f.station_id = ? AND w.is_active = 1 AND f.is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapWitness(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void displayWitnessesByStationId(int stationId) {
        LinkedList<Witness> list = getWitnessesByStationId(stationId);
        if (list == null || list.isEmpty()) {
            System.out.println("No witnesses found for station ID: " + stationId);
            return;
        }

        System.out.println("\n+------+----------------------+----------------------+-----------------+--------+");
        System.out.printf("| %-4s | %-20s | %-20s | %-15s | %-6s |\n",
                "ID", "FIR Number", "Full Name", "Contact", "Active");
        System.out.println("+------+----------------------+----------------------+-----------------+--------+");

        for (Witness w : list) {
            String firNum = w.getFirNumber() != null ? w.getFirNumber() : "N/A";
            String fullName = (w.getFirstName() + " " + w.getLastName()).trim();
            if (fullName.isEmpty()) fullName = "N/A";
            if (fullName.length() > 20) fullName = fullName.substring(0, 17) + "...";
            String contact = w.getContact() != null ? w.getContact() : "N/A";
            String active = w.isActive() ? "Yes" : "No";

            System.out.printf("| %-4d | %-20s | %-20s | %-15s | %-6s |\n",
                    w.getId(), firNum, fullName, contact, active);
        }
        System.out.println("+------+----------------------+----------------------+-----------------+--------+");
    }

    public static boolean isWitnessAtStation(int witnessId, int stationId) {
        String sql = "SELECT 1 FROM witnesses w " +
                "JOIN fir f ON w.fir_number = f.fir_number " +
                "WHERE w.id = ? AND f.station_id = ? AND w.is_active = 1 AND f.is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, witnessId);
            ps.setInt(2, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static LinkedList<Witness> getWitnessesByOfficerId(int officerId) {
        LinkedList<Witness> list = new LinkedList<>();
        String sql = "SELECT w.* FROM witnesses w " +
                "JOIN fir f ON w.fir_number = f.fir_number " +
                "WHERE f.assigned_officer_id = ? AND w.is_active = 1 AND f.is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, officerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapWitness(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static void displayWitnessesByOfficerId(int officerId) {
        LinkedList<Witness> list = getWitnessesByOfficerId(officerId);
        if (list == null || list.isEmpty()) {
            System.out.println("No witnesses found for officer ID: " + officerId);
            return;
        }

        System.out.println("\n+------+----------------------+----------------------+-----------------+--------+");
        System.out.printf("| %-4s | %-20s | %-20s | %-15s | %-6s |\n",
                "ID", "FIR Number", "Full Name", "Contact", "Active");
        System.out.println("+------+----------------------+----------------------+-----------------+--------+");

        for (Witness w : list) {
            String firNum = w.getFirNumber() != null ? w.getFirNumber() : "N/A";
            String fullName = (w.getFirstName() + " " + w.getLastName()).trim();
            if (fullName.isEmpty()) fullName = "N/A";
            if (fullName.length() > 20) fullName = fullName.substring(0, 17) + "...";
            String contact = w.getContact() != null ? w.getContact() : "N/A";
            String active = w.isActive() ? "Yes" : "No";

            System.out.printf("| %-4d | %-20s | %-20s | %-15s | %-6s |\n",
                    w.getId(), firNum, fullName, contact, active);
        }
        System.out.println("+------+----------------------+----------------------+-----------------+--------+");
    }

    public static boolean isWitnessAssignedToOfficer(int witnessId, int officerId) {
        String sql = "SELECT 1 FROM witnesses w " +
                "JOIN fir f ON w.fir_number = f.fir_number " +
                "WHERE w.id = ? AND f.assigned_officer_id = ? " +
                "AND w.is_active = 1 AND f.is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, witnessId);
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