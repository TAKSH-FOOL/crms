package com.crms.dao;

import com.crms.Session;
import com.crms.config.DatabaseConnection;
import com.crms.model.CrimeCategory;
import com.crms.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.LinkedList;

public class CrimeCategoryDAO {
   static Scanner sc = new Scanner(System.in);

    public static boolean addCategory(String category) {
        // Check if category already exists
        String checkSql = "SELECT id FROM crime_categories WHERE category = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql);
             ) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Category already exists.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Proceed with insert – ensure table name matches
        String sql = "INSERT INTO crime_categories (category) VALUES (?)"; // fixed spelling
        User current = Session.getCurrentUser();
        int userId = (current != null) ? current.getId() : 0;
        String username = (current != null) ? current.getUsername() : "SYSTEM";

        try (Connection conn = DatabaseConnection.getConnectionWithAudit(userId, username);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            int rowsAffected = ps.executeUpdate();   // 🔥 execute the insert
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static LinkedList getAllCategory() {
        LinkedList<CrimeCategory> list = new LinkedList();
        String sql = "SELECT * FROM crime_categories WHERE active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CrimeCategory cc = new CrimeCategory(rs.getInt("id"), rs.getString("category"));
                list.add(cc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void viewAllCrimeCategory(){
        LinkedList<CrimeCategory> list = getAllCategory();
        for (int i = 0; i<list.size(); i++){
            CrimeCategory temp = list.get(i);
            System.out.println(i+1 + " " + temp.getCategory());
        }
    }


}
