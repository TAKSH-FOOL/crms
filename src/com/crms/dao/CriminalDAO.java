package com.crms.dao;

import com.crms.ds.CriminalBST;
import com.crms.model.Criminal;
import com.crms.config.DatabaseConnection;
import com.crms.model.User;
import com.crms.util.AuditLogger;
import com.crms.Session;

import java.sql.*;
import java.util.LinkedList;

public class CriminalDAO {

    public static boolean create(Criminal criminal) {
        String sql = "INSERT INTO criminals (first_name, last_name, dob, gender, address, phone, wanted_status, is_active) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, criminal.getFirstName());
            ps.setString(2, criminal.getLastName());
            ps.setDate(3, criminal.getDob() != null ? Date.valueOf(criminal.getDob()) : null);
            ps.setString(4, criminal.getGender());
            ps.setString(5, criminal.getAddress());
            ps.setString(6, criminal.getPhone());
            ps.setString(7, criminal.getWantedStatus());
            ps.setBoolean(8, criminal.isActive());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) criminal.setId(rs.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create criminal: " + e.getMessage());
            return false;
        }
    }

    public static Criminal getById(int id) {
        String sql = "SELECT * FROM criminals WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCriminal(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LinkedList<Criminal> getAllCriminal() {
        LinkedList<Criminal> list = new LinkedList();
        String sql = "SELECT * FROM criminals";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapCriminal(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(Criminal criminal) {
        String sql = "UPDATE criminals SET first_name=?, last_name=?, dob=?, gender=?, address=?, phone=?, " +
                "wanted_status=?, is_active=? WHERE id=?";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username)) {
            Criminal old = null;
            String selectOld = "SELECT * FROM criminals WHERE id = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                 ) {
                psOld.setInt(1, criminal.getId());
                try (ResultSet rsOld = psOld.executeQuery()) {
                    if (rsOld.next()) old = mapCriminal(rsOld);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, criminal.getFirstName());
                ps.setString(2, criminal.getLastName());
                ps.setDate(3, criminal.getDob() != null ? Date.valueOf(criminal.getDob()) : null);
                ps.setString(4, criminal.getGender());
                ps.setString(5, criminal.getAddress());
                ps.setString(6, criminal.getPhone());
                ps.setString(7, criminal.getWantedStatus());
                ps.setBoolean(8, criminal.isActive());
                ps.setInt(9, criminal.getId());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update criminal: " + e.getMessage());
            return false;
        }
    }


    public static boolean linkCriminalToCrime(String crimeNumber, int criminalId, String role) {
        String sql = "INSERT INTO crime_criminal_mapping (crime_number, criminal_id, role) VALUES (?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, crimeNumber);
            ps.setInt(2, criminalId);
            ps.setString(3, role);
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            AuditLogger.log(Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0,
                    Session.getCurrentUser() != null ? Session.getCurrentUser().getUsername() : "SYSTEM",
                    "LINK_CRIMINAL_CRIME", "crime_criminal_mapping", crimeNumber + "-" + criminalId,
                    null, "role=" + role);
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to link criminal to crime: " + e.getMessage());
            return false;
        }
    }



    private static Criminal mapCriminal(ResultSet rs) throws SQLException {
        Criminal c = new Criminal();
        c.setId(rs.getInt("id"));
        c.setFirstName(rs.getString("first_name"));
        c.setLastName(rs.getString("last_name"));
        Date d = rs.getDate("dob");
        if (d != null) c.setDob(d.toLocalDate());
        c.setGender(rs.getString("gender"));
        c.setAddress(rs.getString("address"));
        c.setPhone(rs.getString("phone"));
        c.setWantedStatus(rs.getString("wanted_status"));
        c.setActive(rs.getBoolean("is_active"));
        return c;
    }

    static public void displayAllCriminal(){
        LinkedList<Criminal> all = getAllCriminal();
        for (int idx = 0; idx < all.size(); idx++) {
            Criminal c = (Criminal) all.get(idx);
            System.out.println(c);
        }
    }

    public static void displayCriminalTable(LinkedList<Criminal> criminals) {
        if (criminals == null || criminals.isEmpty()) {
            System.out.println("No criminals found.");
            return;
        }

        System.out.println("\n+------+----------------------+----------------+--------+");
        System.out.printf("| %-4s | %-20s | %-14s | %-6s |\n",
                "ID", "Full Name", "Wanted Status", "Active");
        System.out.println("+------+----------------------+----------------+--------+");

        for (Criminal c : criminals) {
            String fullName = (c.getFirstName() + " " + c.getLastName()).trim();
            if (fullName.isEmpty()) fullName = "N/A";
            if (fullName.length() > 20) fullName = fullName.substring(0, 17) + "...";

            String status = c.getWantedStatus() != null ? c.getWantedStatus() : "N/A";
            String active = c.isActive() ? "Yes" : "No";

            System.out.printf("| %-4d | %-20s | %-14s | %-6s |\n",
                    c.getId(), fullName, status, active);
        }
        System.out.println("+------+----------------------+----------------+--------+");
    }


    public static LinkedList<Criminal> getCriminalsByStationId() {
        LinkedList<Criminal> list = new LinkedList<>();
        String sql = "SELECT DISTINCT c.* FROM criminals c " +
                "JOIN crime_criminal_mapping ccm ON c.id = ccm.criminal_id " +
                "JOIN crime_records cr ON ccm.crime_number = cr.crime_number " +
                "JOIN fir f ON cr.fir_number = f.fir_number " +
                "WHERE c.is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCriminal(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static LinkedList<Criminal> searchCriminal(String firstName, String lastName, String wantedStatus) {
        // Build a fresh BST from the database (only active criminals)
        CriminalBST bst = new CriminalBST();
        String sql = "SELECT * FROM criminals WHERE is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                bst.insert(mapCriminal(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }

        // Perform search using the BST
        return bst.searchCriminals(firstName, lastName, wantedStatus);
    }

}