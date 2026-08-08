package com.crms.dao;

import com.crms.model.User;
import com.crms.model.Victim;
import com.crms.config.DatabaseConnection;
import com.crms.Session;

import java.sql.*;
import java.util.LinkedList;

public class VictimDAO {

    public static boolean create(Victim victim) {
        String sql = "INSERT INTO victims (fir_number, first_name, last_name, contact, address, harm_description) VALUES (?,?,?,?,?,?)";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, victim.getFirNumber());
            ps.setString(2, victim.getFirstName());
            ps.setString(3, victim.getLastName());
            ps.setString(4, victim.getContact());
            ps.setString(5, victim.getAddress());
            ps.setString(6, victim.getHarm_description());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) victim.setId(rs.getInt(1));
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create victim: " + e.getMessage());
            return false;
        }
    }

    public static Victim getById(int id) {
        String sql = "SELECT * FROM victims WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapVictim(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean update(Victim victim) {
        String sql = "UPDATE victims SET fir_number=?, first_name=?, last_name=?, contact=?, address=? WHERE id=?";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username)) {
            Victim old = null;
            String selectOld = "SELECT * FROM victims WHERE id = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                 ) {
                psOld.setInt(1, victim.getId());
                try (ResultSet rsOld = psOld.executeQuery()) {
                    if (rsOld.next()) old = mapVictim(rsOld);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, victim.getFirNumber());
                ps.setString(2, victim.getFirstName());
                ps.setString(3, victim.getLastName());
                ps.setString(4, victim.getContact());
                ps.setString(5, victim.getAddress());
                ps.setInt(6, victim.getId());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;

                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update victim: " + e.getMessage());
            return false;
        }
    }


    public static LinkedList<Victim> getAllActive() {
        LinkedList<Victim> list = new LinkedList<>();
        String sql = "SELECT * FROM victims WHERE is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapVictim(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Victim mapVictim(ResultSet rs) throws SQLException {
        Victim v = new Victim();
        v.setId(rs.getInt("id"));
        v.setFirNumber(rs.getString("fir_number"));
        v.setFirstName(rs.getString("first_name"));
        v.setLastName(rs.getString("last_name"));
        v.setContact(rs.getString("contact"));
        v.setAddress(rs.getString("address"));
        v.setActive("true".equals(rs.getString("is_active")));
        return v;
    }

    public static LinkedList<Victim> getVictimsByStationId(int stationId) {
        LinkedList<Victim> list = new LinkedList<>();
        String sql = "{CALL GetVictimsByStation(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, stationId);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    list.add(mapVictim(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isVictimAtStation(int victimId, int stationId) {
        String sql = "SELECT 1 FROM victims v " +
                "JOIN fir f ON v.fir_number = f.fir_number " +
                "WHERE v.id = ? AND f.station_id = ? AND v.is_active = 1 AND f.is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, victimId);
            ps.setInt(2, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // returns true if any row found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all active victims linked to FIRs assigned to the given officer.
     */
    public static LinkedList<Victim> getVictimsByOfficerId(int officerId) {
        LinkedList<Victim> list = new LinkedList<>();
        String sql = "SELECT v.* FROM victims v " +
                "JOIN fir f ON v.fir_number = f.fir_number " +
                "WHERE f.assigned_officer_id = ? AND v.is_active = 1 AND f.is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, officerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapVictim(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void displayVictimsByOfficerId(int officerId) {
        LinkedList<Victim> list = getVictimsByOfficerId(officerId);
        if (list == null || list.isEmpty()) {
            System.out.println("No victims found for officer ID: " + officerId);
            return;
        }

        System.out.println("\n+------+----------------------+----------------------+-----------------+--------+");
        System.out.printf("| %-4s | %-20s | %-20s | %-15s | %-6s |\n",
                "ID", "FIR Number", "Full Name", "Contact", "Active");
        System.out.println("+------+----------------------+----------------------+-----------------+--------+");

        for (Victim v : list) {
            String firNum = v.getFirNumber() != null ? v.getFirNumber() : "N/A";
            String fullName = (v.getFirstName() + " " + v.getLastName()).trim();
            if (fullName.isEmpty()) fullName = "N/A";
            if (fullName.length() > 20) fullName = fullName.substring(0, 17) + "...";
            String contact = v.getContact() != null ? v.getContact() : "N/A";
            String active = v.isActive() ? "Yes" : "No";

            System.out.printf("| %-4d | %-20s | %-20s | %-15s | %-6s |\n",
                    v.getId(), firNum, fullName, contact, active);
        }
        System.out.println("+------+----------------------+----------------------+-----------------+--------+");
    }

    public static boolean isVictimAssignedToOfficer(int victimId, int officerId) {
        String sql = "SELECT 1 FROM victims v " +
                "JOIN fir f ON v.fir_number = f.fir_number " +
                "WHERE v.id = ? AND f.assigned_officer_id = ? " +
                "AND v.is_active = 1 AND f.is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, victimId);
            ps.setInt(2, officerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void displayVictimsByStationId(int stationId) {
        LinkedList<Victim> list = getVictimsByStationId(stationId);
        if (list == null || list.isEmpty()) {
            System.out.println("No victims found for station ID: " + stationId);
            return;
        }

        System.out.println("\n+------+----------------------+----------------------+-----------------+--------+");
        System.out.printf("| %-4s | %-20s | %-20s | %-15s | %-6s |\n",
                "ID", "FIR Number", "Full Name", "Contact", "Active");
        System.out.println("+------+----------------------+----------------------+-----------------+--------+");

        for (Victim v : list) {
            String firNum = v.getFirNumber() != null ? v.getFirNumber() : "N/A";
            String fullName = (v.getFirstName() + " " + v.getLastName()).trim();
            if (fullName.isEmpty()) fullName = "N/A";
            if (fullName.length() > 20) fullName = fullName.substring(0, 17) + "...";
            String contact = v.getContact() != null ? v.getContact() : "N/A";
            String active = v.isActive() ? "Yes" : "No";

            System.out.printf("| %-4d | %-20s | %-20s | %-15s | %-6s |\n",
                    v.getId(), firNum, fullName, contact, active);
        }
        System.out.println("+------+----------------------+----------------------+-----------------+--------+");
    }
}