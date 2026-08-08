package com.crms.dao;

import com.crms.ds.UserBST;
import com.crms.model.User;
import com.crms.config.DatabaseConnection;
import com.crms.Session;

import java.sql.*;
import java.util.LinkedList;


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
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username);
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
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username)) {

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

                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update user '" + user.getUsername() + "': " + e.getMessage());
            return false;
        }
    }

    public static LinkedList<User> searchByRole(String role) {
        // Build the BST from all active users
        UserBST bst = new UserBST();
        String sql = "SELECT * FROM users WHERE active = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = mapUser(rs);
                bst.insert(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new LinkedList(); // empty list
        }

        return bst.searchByRole(role);
    }

    public static boolean isUserNameCurrentStationId(String username) {
        User user = getByUsername(username);
        if (user.getStationId() == Session.getCurrentUser().getStationId()){
            return true;
        }
        return false;
    }

    public static boolean isNonAdmin(String username) {
        User user = getByUsername(username);
        if (user == null) return false;
        return !"ADMIN".equalsIgnoreCase(user.getRole());
    }

    public static LinkedList<User> getAllActive() {
        LinkedList<User> list = new LinkedList<>();
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

    public static void viewAllUserByStationId(){
        LinkedList list = getAllUserByStationId();
        for (int i= 0; i< list.size(); i++){
            User temp = (User) list.get(i);
            System.out.println(i+1 + " " + temp.getFullName() );
        }
    }



    public static LinkedList<User> getAllUserByStationId(){
        String sql = "select * from users where station_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Set the station_id parameter
            ps.setInt(1, Session.getCurrentUser().getStationId());
            ResultSet rs = ps.executeQuery();
            LinkedList<User> list = new LinkedList<>();
            while (rs.next()) {
                list.add(mapUser(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public static LinkedList<User> getUsersByStation(int stationId, String role, boolean activeOnly) {
        LinkedList<User> list = new LinkedList<>();
        String sql = "{CALL GetUsersByStation(?, ?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, stationId);
            cs.setString(2, role);
            cs.setByte(3, (byte) (activeOnly ? 1 : 0));
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    list.add(mapUser(rs));
                }
            }
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
        u.setEmail(rs.getString("email"));
        return u;
    }

    // In UserDAO.java
    public static LinkedList<User> getAll() {
        LinkedList<User> list = new LinkedList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isUsernameAvailable(String username) {
        return getByUsername(username.trim()) == null;
    }
}