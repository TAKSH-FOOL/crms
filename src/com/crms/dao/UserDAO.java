package com.crms.dao;

import com.crms.model.User;
import com.crms.config.DatabaseConnection;
import com.crms.util.AuditLogger;
import com.crms.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static User authenticate(String username, String password) {
        String sql =
                "SELECT * FROM users WHERE username = ? AND password = ? AND active = true";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean create(User user) {
        String sql = "INSERT INTO users (username, password, role, station_id, full_name, active) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setInt(4, user.getStationId());
            ps.setString(5, user.getFullName());
            ps.setBoolean(6, user.isActive());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) user.setId(rs.getInt(1));
            }
            AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                    Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                    "CREATE_USER", "users", user.getUsername(), null, user.toString());
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create user '" + user.getUsername() + "': " + e.getMessage());
            return false;
        }
    }

    public static User getById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User getByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()){
                  if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean update(User user) {
        String sql = "UPDATE users SET username=?, password=?, role=?, station_id=?, full_name=?, active=? WHERE id=?";
        // Use a single connection to fetch old and perform update
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Fetch old state within the same transaction
            User old = null;
            String selectOld = "SELECT * FROM users WHERE id = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                ) {
                psOld.setInt(1, user.getId());
                try ( ResultSet rsOld = psOld.executeQuery()){
                    if (rsOld.next()) old = mapUser(rsOld);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getRole());
                ps.setInt(4, user.getStationId());
                ps.setString(5, user.getFullName());
                ps.setBoolean(6, user.isActive());
                ps.setInt(7, user.getId());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "UPDATE_USER", "users", user.getUsername(),
                        old != null ? old.toString() : null, user.toString());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update user '" + user.getUsername() + "': " + e.getMessage());
            return false;
        }
    }

    public static boolean delete(int userId) {
        String sql = "UPDATE users SET active = false WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            User old = getById(userId); // using separate connection is acceptable for deactivation
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                        Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                        "DELETE_USER", "users", String.valueOf(userId),
                        old != null ? old.toString() : null, "active=false");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to deactivate user ID " + userId + ": " + e.getMessage());
            return false;
        }
    }

    public static List<User> searchByRole(String role) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? AND active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
            ) {
            ps.setString(1, role);
            try ( ResultSet rs = ps.executeQuery()){
                while (rs.next()) list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<User> getAllActive() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setStationId(rs.getInt("station_id"));
        u.setFullName(rs.getString("full_name"));
        u.setActive(rs.getBoolean("active"));
        return u;
    }
}