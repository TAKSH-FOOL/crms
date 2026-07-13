package com.crms;

import com.crms.model.User;
import com.crms.dao.UserDAO;
import com.crms.util.LoginTracker;
import com.crms.menu.AdminMenu;
import com.crms.menu.OfficerMenu;
import com.crms.menu.StaffMenu;
import com.crms.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Database connection successful.");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return;
        }

        while (true) {
            System.out.println("\n===== CRIME RECORDS MANAGEMENT SYSTEM =====");
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = UserDAO.authenticate(username, password);
            if (user != null) {
                LoginTracker.recordLogin(username, "127.0.0.1", true, null);
                Session.setCurrentUser(user);
                System.out.println("Welcome, " + user.getFullName() + " (" + user.getRole() + ")");
                try {
                    switch (user.getRole()) {
                        case "ADMIN":
                            AdminMenu.show();
                            break;
                        case "OFFICER":
                            OfficerMenu.show();
                            break;
                        case "STAFF":
                            StaffMenu.show();
                            break;
                        default:
                            System.out.println("Unknown role.");
                    }
                } catch (Exception e) {
                    System.err.println("An unexpected error occurred: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    Session.clear();
                }
            } else {
                LoginTracker.recordLogin(username, "127.0.0.1", false, "Invalid credentials");
                System.out.println("Invalid username or password.");
                System.out.print("Try again? (y/n): ");
                String again = scanner.nextLine();
                if (!again.equalsIgnoreCase("y")) {
                    break;
                }
            }
        }
        System.out.println("System shutdown.");
    }
}