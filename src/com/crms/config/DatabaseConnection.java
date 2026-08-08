package com.crms.config;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/crms_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // change to your MySQL password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    public static Connection getConnectionWithAudit(int userId, String username) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SET @app_user_id = ?, @app_username = ?")) {
            ps.setInt(1, userId);
            ps.setString(2, username);
            ps.execute();
        }
        return conn;
    }
}