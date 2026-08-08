package com.crms.dao;

import com.crms.model.Officer;
import com.crms.config.DatabaseConnection;
import com.crms.model.User;
import com.crms.Session;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Scanner;

public class OfficerDAO {
    static Scanner sc = new Scanner(System.in);

    public static boolean create(Officer officer) {
        String sql = "INSERT INTO officers (user_id, officer_rank, station_id) VALUES (?,?,?)";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, officer.getUserId());
            ps.setString(2, officer.getOfficerRank());
            ps.setInt(3, officer.getStationId());
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    officer.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create officer: " + e.getMessage());
            return false;
        }
    }

    public static LinkedList<Officer> getAllByStationId(int stationId) {
        LinkedList<Officer> list = new LinkedList<>();
        String sql = "SELECT * FROM officers WHERE station_id = ? AND is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapOfficer(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static boolean updateBadgeNumberAndEmail(Officer officer) {
        int year = LocalDate.now().getYear();
        String finalBadge = year + String.format("%05d", officer.getId());
        String email = "OFFICER" + finalBadge + "@police.gov.in";

        String sqlBadge = "UPDATE officers SET badge_number = ? WHERE id = ?";
        String sqlEmail = "UPDATE users SET email = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlBadge);
                 PreparedStatement ps2 = conn.prepareStatement(sqlEmail)) {
                ps1.setString(1, finalBadge);
                ps1.setInt(2, officer.getId());
                ps1.executeUpdate();

                ps2.setString(1, email);
                ps2.setInt(2, officer.getUserId());
                ps2.executeUpdate();

                conn.commit();
                officer.setBadgeNumber(finalBadge);
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    public static LinkedList<Officer> getAll() {
        LinkedList<Officer> list = new LinkedList<>();
        String sql = "SELECT * FROM officers where officer_rank in ('SUB_INSPECTOR','SUPERINTENDENT') ";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapOfficer(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static LinkedList<Officer> getUnassignedOfficersByStation(int stationId) {
        LinkedList<Officer> list = new LinkedList<>();
        String sql = "SELECT o.* FROM officers o " +
                "WHERE o.station_id = ? AND o.is_active = 1 " +
                "AND NOT EXISTS (SELECT 1 FROM fir f " +
                "WHERE f.assigned_officer_id = o.id " +
                "AND f.is_active = 1 " +
                "AND f.status IN ('ASSIGNED', 'INVESTIGATING'))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOfficer(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean update(Officer officer) {
        String sql = "UPDATE officers SET user_id=?, badge_number=?, officer_rank=? WHERE id=?";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username)) {
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
                System.out.println("Enter Officer Post ");
                System.out.println("1. CONSTABLE\n" +
                        "2. SUB_INSPECTOR\n" +
                        "3. INSPECTOR\n" +
                        "4. SUPERINTENDENT");
                System.out.print("enter your choice : ");
                int choice = sc.nextInt();
                String officerRole;
                if (choice == 1) {
                    officerRole = "CONSTABLE";
                } else if (choice == 2) {
                    officerRole = "SUB_INSPECTOR";
                } else if (choice == 3) {
                    officerRole = "INSPECTOR";
                } else if (choice == 4) {
                    officerRole = "SUPERINTENDENT";
                } else {
                    System.out.println("Invalid officer rank.");
                    return false;
                }
                ps.setString(3, officerRole);
                ps.setInt(4, officer.getId());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;

                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update officer: " + e.getMessage());
            return false;
        }
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