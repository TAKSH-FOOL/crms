package com.crms.dao;

import com.crms.ds.FIRBST;
import com.crms.model.FIR;
import com.crms.config.DatabaseConnection;
import com.crms.model.User;
import com.crms.Session;

import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;


public class FIRDAO {

    public static boolean create(FIR fir) {
        String sql = "INSERT INTO fir ( complainant_name, complainant_contact, incident_location, " +
                "incident_date, incident_description, crime_category, status, assigned_officer_id, station_id, is_active) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fir.getComplainantName());
            ps.setString(2, fir.getComplainantContact());
            ps.setString(3, fir.getIncidentLocation());
            ps.setTimestamp(4, Timestamp.valueOf(fir.getIncidentDate()));
            ps.setString(5, fir.getIncidentDescription());
            ps.setString(6, fir.getCrimeCategory());
            ps.setString(7, fir.getStatus());

            // --- FIX: use setObject for nullable Integer ---
            ps.setObject(8, fir.getAssignedOfficerId()); // can be null

            ps.setInt(9, fir.getStationId());
            ps.setBoolean(10, fir.isActive());
            int id;
            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    fir.setId(rs.getInt(1));
                    id = rs.getInt(1);
                    String code = "FIR-"+ PoliceStationDAO.stationCodeGenerator(PoliceStationDAO.getStationNameById(fir.getStationId()))+"-"+ id;
                    String sql1 = "update fir set fir_number = ? where id = ?";
                    try (PreparedStatement pst = conn.prepareStatement(sql1);){
                        pst.setString(1,code);
                        pst.setInt(2,id);
                        pst.executeUpdate();
                        fir.setFirNumber(code);
                    }
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Failed to create FIR: " + e.getMessage());
            return false;
        }
    }


    public static FIR getByFIRNumber(String firNumber) {
        String sql = "SELECT * FROM fir WHERE fir_number = ?";
        LinkedList<FIR> list = new LinkedList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
            ps.setString(1, firNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFIR(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while fetching FIR by number: " + e.getMessage());
        }
        return list.isEmpty() ? null : list.getFirst(); // Assuming the list will have at most one element
    }

    public static boolean isFIRCurrentUserStation(String firNumber){
        String sql = "SELECT * FROM fir WHERE fir_number = ?";
        LinkedList<FIR> list = new LinkedList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, firNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFIR(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while fetching FIR by number: " + e.getMessage());
            return false;
        }
        if (list.get(0).getStationId() == Session.getCurrentUser().getStationId()) {
            return true;
        }
        return false;
    }

    public static LinkedList<FIR> getByOfficerId(int officerId) {
        LinkedList<FIR> list = new LinkedList<>();
        String sql = "SELECT * FROM fir WHERE assigned_officer_id = ? AND is_active = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ) {
                ps.setInt(1, officerId);
                ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FIR fir = mapFIR(rs);
                list.add(fir);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }

        return list;
    }

    public static boolean isFIRAssignedToOfficer(String firNumber, int officerId) {
        if (firNumber == null || firNumber.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT 1 FROM fir WHERE fir_number = ? AND assigned_officer_id = ? AND is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firNumber);
            ps.setInt(2, officerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static LinkedList search(String status, String complainant, String fromDate, String toDate) {
        // 1. Build the BST from all active FIRs
        FIRBST bst = new FIRBST();
        String sql = "SELECT * FROM fir WHERE is_active = true and station_id = " + Session.getCurrentUser().getStationId();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                FIR fir = mapFIR(rs);
                bst.insert(fir);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new LinkedList(); // empty list
        }

        // 2. Perform the search using the BST
        return bst.search(status, complainant, fromDate, toDate);
    }

    public static void displayFIRNumbersByOfficerId(int officerId) {
        LinkedList<FIR> numbers = getByOfficerId(officerId);
        if (numbers.isEmpty()) {
            System.out.println("No FIRs assigned to this officer.");
        } else {
            System.out.println("Assigned FIR Numbers:");
            for (int i = 0; i < numbers.size(); i++) {
                System.out.println((i + 1) + ". " + numbers.get(i).getFirNumber());
            }
        }
    }

    public static void displayFIRNumbersByStationId(int stationId) {
        LinkedList<FIR> numbers = getByStationId(stationId);
        if (numbers.isEmpty()) {
            System.out.println("No FIRs assigned to this station.");
        } else {
            for (int i = 0; i < numbers.size(); i++) {
                System.out.println(i + 1);
                System.out.println(numbers.get(i));
            }
        }
    }

    public static LinkedList<FIR> getByStationId(int id){
        String sql = "SELECT * FROM fir WHERE station_id = ? ";
        LinkedList<FIR> list = new LinkedList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFIR(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static LinkedList<FIR> getAllByStationId(int id){
        String sql = "SELECT * FROM fir WHERE station_id = ?";
        LinkedList<FIR> list = new LinkedList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFIR(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static LinkedList<FIR> getAllActive() {
        // Build a BST from all active FIRs
        String sql = "SELECT * FROM fir WHERE is_active = true";
        LinkedList<FIR> list = new LinkedList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                FIR fir = mapFIR(rs);
                list.add(fir);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new LinkedList(); // empty list
        }

        // Return all FIRs in sorted order (by FIR number)
                return list;
    }

    public static boolean update(FIR fir) {
        String sql = "UPDATE fir SET complainant_name=?, complainant_contact=?, incident_location=?, " +
                "incident_date=?, incident_description=?, crime_category=?, status=?, assigned_officer_id=?, station_id=? " +
                "WHERE id=?";
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";
        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username)) {
            FIR old = null;
            String selectOld = "SELECT * FROM fir WHERE id = ?";
            try (PreparedStatement psOld = conn.prepareStatement(selectOld);
                 ) {
                psOld.setInt(1, fir.getId());
                try (ResultSet rsOld = psOld.executeQuery()) {
                    if (rsOld.next()) old = mapFIR(rsOld);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, fir.getComplainantName());
                ps.setString(2, fir.getComplainantContact());
                ps.setString(3, fir.getIncidentLocation());
                ps.setTimestamp(4, Timestamp.valueOf(fir.getIncidentDate()));
                ps.setString(5, fir.getIncidentDescription());
                ps.setString(6, fir.getCrimeCategory());
                ps.setString(7, fir.getStatus());
                if (fir.getAssignedOfficerId() != null) ps.setInt(8, fir.getAssignedOfficerId());
                else ps.setNull(8, Types.INTEGER);
                ps.setInt(9, fir.getStationId());
                ps.setInt(10, fir.getId());
                int rows = ps.executeUpdate();
                if (rows == 0) return false;

                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to update FIR: " + e.getMessage());
            return false;
        }
    }

    private static FIR mapFIR(ResultSet rs) throws SQLException {
        FIR f = new FIR();
        f.setId(rs.getInt("id"));
        f.setFirNumber(rs.getString("fir_number"));
        f.setComplainantName(rs.getString("complainant_name"));
        f.setComplainantContact(rs.getString("complainant_contact"));
        f.setIncidentLocation(rs.getString("incident_location"));
        Timestamp ts = rs.getTimestamp("incident_date");
        if (ts != null) f.setIncidentDate(ts.toLocalDateTime());
        f.setIncidentDescription(rs.getString("incident_description"));
        f.setCrimeCategory(rs.getString("crime_category"));
        f.setStatus(rs.getString("status"));
        int assignedId = rs.getInt("assigned_officer_id");
        if (!rs.wasNull()) f.setAssignedOfficerId(assignedId);
        f.setStationId(rs.getInt("station_id"));
        f.setActive(rs.getBoolean("is_active"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) f.setCreatedAt(created.toLocalDateTime());
        return f;
    }

    public static boolean hasAnyFIR() {
        String sql = "SELECT COUNT(*) FROM fir";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void displayAllActiveFIRNumber() {
        LinkedList<FIR> list = getAllActive();
        if (list.isEmpty()) {
            System.out.println("No active FIRs found.");
            return;
        }
        int i = 1;
        Iterator<FIR> it = list.iterator();
        while (it.hasNext()) {
            FIR f = it.next();
            System.out.println(i++ + ". " + f.getFirNumber());
        }
    }

    public static void displayAllFIRNumber() {
        LinkedList<FIR> list = getAllFir();
        if (list.isEmpty()) {
            System.out.println("No FIRs found.");
            return;
        }
        int i = 1;
        Iterator<FIR> it = list.iterator();
        while (it.hasNext()) {
            FIR f = it.next();
            System.out.println(i++ + ". " + f.getFirNumber());
        }
    }

    public static LinkedList<FIR> getAllFir() {
        String sql = "SELECT * FROM fir"; // no is_active filter
        LinkedList<FIR> list = new LinkedList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                FIR fir = mapFIR(rs);
                list.add(fir);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new LinkedList();
        }

        return list;
    }

    public static LinkedList<FIR> getAllInAssignedFIR(){
        String sql = "select * from fir where assigned_officer_id is null";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            LinkedList<FIR> firList = new LinkedList<>();
            while (rs.next()) {
                FIR fir = mapFIR(rs);
                firList.add(fir);
            }
            return firList;
        } catch (SQLException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public static void displayAllInAssignedFIRNumber() {
        LinkedList<FIR> list = getAllInAssignedFIR();
        for (int i = 0; i < list.size(); i++) {
            FIR fir = list.get(i);
            System.out.println((i + 1) + ". " + fir.getFirNumber());
        }
    }public static LinkedList getAllInAssignedFIRByStation(int stationId) {
        LinkedList firList = new LinkedList();
        String sql = "{CALL GetUnassignedFIRsByStation(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, stationId);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    FIR fir = mapFIR(rs);
                    firList.add(fir);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return firList;
    }

    public static boolean isFIRAtStation(String firNumber, int stationId) {
        String sql = "{CALL IsFIRAtStation(?, ?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, firNumber);
            cs.setInt(2, stationId);
            cs.registerOutParameter(3, Types.TINYINT);
            cs.execute();
            return cs.getByte(3) == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void displayAllInAssignedFIRByStation(int stationId) {
        LinkedList<FIR> list = getAllInAssignedFIRByStation(stationId);
        if (list.isEmpty()){
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            FIR fir = list.get(i);
            System.out.println((i + 1) + ". " + fir.getFirNumber());
        }
    }



}