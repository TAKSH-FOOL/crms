package com.crms.dao;

import com.crms.model.LoginHistory;
import com.crms.config.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoginHistoryDAO {

    public static List<LoginHistory> getAll() {
        List<LoginHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM login_history ORDER BY login_time DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapHistory(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<LoginHistory> getByUsername(String username) {
        List<LoginHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM login_history WHERE username = ? ORDER BY login_time DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapHistory(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static LoginHistory mapHistory(ResultSet rs) throws SQLException {
        LoginHistory h = new LoginHistory();
        h.setId(rs.getInt("id"));
        h.setUsername(rs.getString("username"));
        Timestamp ts = rs.getTimestamp("login_time");
        if (ts != null) h.setLoginTime(ts.toLocalDateTime());
        h.setIpAddress(rs.getString("ip_address"));
        h.setSuccess(rs.getBoolean("success"));
        h.setFailureReason(rs.getString("failure_reason"));
        return h;
    }
}