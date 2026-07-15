package com.crms.dao;

import com.crms.model.Officer;
import com.crms.config.DatabaseConnection;
import com.crms.util.AuditLogger;
import com.crms.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OfficerDAO {

    public static boolean create(Officer officer) {
        String sql = "INSERT INTO officers (user_id, badge_number, officer_rank) VALUES (?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, officer.getUserId());
            ps.setString(2, officer.getBadgeNumber());
            ps.setString(3, officer.getOfficerRank());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) officer.setId(rs.getInt(1));
            }
            AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                    Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                    "CREATE_OFFICER", "officers", officer.getBadgeNumber(), null, officer.toString());
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create officer: " + e.getMessage());
            return false;
        }
    }

    public static Officer getById(int id) {
        String sql = "SELECT * FROM officers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapOfficer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Officer getByUserId(int userId) {
        String sql = "SELECT * FROM officers WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapOfficer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Officer> getAll() {
        List<Officer> list = new ArrayList<>();
        String sql = "SELECT * FROM officers";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapOfficer(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(Officer officer) {
        String sql = "UPDATE officers SET user_id=?, badge_number=?, officer_rank=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Fetch old
            Officer old = null;
            String selectOld = "SELECT * FROM officers WHERE id = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                 ) {
                psOld.setInt(1, officer.getId());
                try (ResultSet rsOld = psOld.executeQuery()) {
                    if (rsOld.next()) old = mapOfficer(rsOld);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, officer.getUserId());
                ps.setString(2, officer.getBadgeNumber());
                ps.setString(3, officer.getOfficerRank());
                ps.setInt(4, officer.getId());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "UPDATE_OFFICER", "officers", String.valueOf(officer.getId()),
                        old != null ? old.toString() : null, officer.toString());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update officer: " + e.getMessage());
            return false;
        }
    }

    // No soft delete – deactivate user instead
    public static boolean delete(int officerId) {
        Officer officer = getById(officerId);
        if (officer == null) {
            return false;
        }
        return UserDAO.delete(officer.getUserId());
    }

    private static Officer mapOfficer(ResultSet rs) throws SQLException {
        Officer o = new Officer();
        o.setId(rs.getInt("id"));
        o.setUserId(rs.getInt("user_id"));
        o.setBadgeNumber(rs.getString("badge_number"));
        o.setOfficerRank(rs.getString("officer_rank"));
        return o;
    }
}