package com.crms.dao;

import com.crms.model.Witness;
import com.crms.config.DatabaseConnection;
import com.crms.util.AuditLogger;
import com.crms.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WitnessDAO {

    public static boolean create(Witness witness) {
        String sql = "INSERT INTO witnesses (fir_number, first_name, last_name, contact, statement) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
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
            AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                    Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                    "CREATE_WITNESS", "witnesses", witness.getFirNumber(), null, witness.toString());
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

    public static List<Witness> getByFIRNumber(String firNumber) {
        List<Witness> list = new ArrayList<>();
        String sql = "SELECT * FROM witnesses WHERE fir_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setString(1, firNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapWitness(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(Witness witness) {
        String sql = "UPDATE witnesses SET fir_number=?, first_name=?, last_name=?, contact=?, statement=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
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
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "UPDATE_WITNESS", "witnesses", String.valueOf(witness.getId()),
                        old != null ? old.toString() : null, witness.toString());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update witness: " + e.getMessage());
            return false;
        }
    }

    public static boolean delete(int witnessId) {
        String sql = "DELETE FROM witnesses WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            Witness old = getById(witnessId);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, witnessId);
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "DELETE_WITNESS", "witnesses", String.valueOf(witnessId),
                        old != null ? old.toString() : null, "deleted");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to delete witness: " + e.getMessage());
            return false;
        }
    }

    private static Witness mapWitness(ResultSet rs) throws SQLException {
        Witness w = new Witness();
        w.setId(rs.getInt("id"));
        w.setFirNumber(rs.getString("fir_number"));
        w.setFirstName(rs.getString("first_name"));
        w.setLastName(rs.getString("last_name"));
        w.setContact(rs.getString("contact"));
        w.setStatement(rs.getString("statement"));
        return w;
    }
}