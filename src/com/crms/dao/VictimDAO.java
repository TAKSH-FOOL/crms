package com.crms.dao;

import com.crms.model.Victim;
import com.crms.config.DatabaseConnection;
import com.crms.util.AuditLogger;
import com.crms.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VictimDAO {

    public static boolean create(Victim victim) {
        String sql = "INSERT INTO victims (fir_number, first_name, last_name, contact, address) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, victim.getFirNumber());
            ps.setString(2, victim.getFirstName());
            ps.setString(3, victim.getLastName());
            ps.setString(4, victim.getContact());
            ps.setString(5, victim.getAddress());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) victim.setId(rs.getInt(1));
            }
            AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                    Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                    "CREATE_VICTIM", "victims", victim.getFirNumber(), null, victim.toString());
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

    public static List<Victim> getByFIRNumber(String firNumber) {
        List<Victim> list = new ArrayList<>();
        String sql = "SELECT * FROM victims WHERE fir_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setString(1, firNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapVictim(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(Victim victim) {
        String sql = "UPDATE victims SET fir_number=?, first_name=?, last_name=?, contact=?, address=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
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
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "UPDATE_VICTIM", "victims", String.valueOf(victim.getId()),
                        old != null ? old.toString() : null, victim.toString());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update victim: " + e.getMessage());
            return false;
        }
    }

    // Hard delete (original behavior)
    public static boolean delete(int victimId) {
        String sql = "DELETE FROM victims WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            Victim old = getById(victimId);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, victimId);
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "DELETE_VICTIM", "victims", String.valueOf(victimId),
                        old != null ? old.toString() : null, "deleted");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to delete victim: " + e.getMessage());
            return false;
        }
    }

    private static Victim mapVictim(ResultSet rs) throws SQLException {
        Victim v = new Victim();
        v.setId(rs.getInt("id"));
        v.setFirNumber(rs.getString("fir_number"));
        v.setFirstName(rs.getString("first_name"));
        v.setLastName(rs.getString("last_name"));
        v.setContact(rs.getString("contact"));
        v.setAddress(rs.getString("address"));
        return v;
    }
}