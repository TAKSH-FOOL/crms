package com.crms.application;

import com.crms.Session;
import com.crms.model.User;
import com.crms.dao.UserDAO;
import com.crms.util.InputHelper;
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
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return;
        }

        while (true) {
            System.out.println("\n===== CRIME RECORDS MANAGEMENT SYSTEM =====");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.print("Enter your choice : ");
            int chioce = InputHelper.readInt(scanner);
            if (chioce == 2) {
                System.out.println("Exiting...");
                break;
            }
            if (chioce != 1){
                System.out.println("invalid input");
                continue;
            }
            int loginChoice = 0;
            while (true){
                System.out.println("===================");
                System.out.println("1. Admin Login");
                System.out.println("2. Officer Login");
                System.out.println("3. Staff Login");
                System.out.print("Enter your choice : ");
                loginChoice = InputHelper.readInt(scanner);
                if (loginChoice < 1 || loginChoice > 3) {
                    System.out.println("invalid input");
                    continue;
                }
                break;
            }

            System.out.println("--------------LOGIN PAGE--------------");
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            User user = UserDAO.authenticate(username, password);
            if (user != null){
                if (loginChoice == 1){
                    if (!user.getRole().equals("ADMIN")) {
                        System.out.println("you are not Admin");
                        continue;
                    }
                }
                if (loginChoice == 2){
                    if (!user.getRole().equals("OFFICER")) {
                        System.out.println("you are not Officer");
                        continue;
                    }
                }
                if (loginChoice == 3){
                    if (!user.getRole().equals("STAFF")) {
                        System.out.println("you are not Staff");
                        continue;
                    }
                }
            }

            if (user != null) {
                LoginTracker.recordLogin(username, true, null);
                Session.setCurrentUser(user);
                System.out.println("Welcome, " + user.getFullName() + " (" + user.getRole() + ")");
                try {
                    switch (user.getRole()) {
                        case "ADMIN" :
                            AdminMenu.show();
                            break;
                        case "OFFICER" :
                            OfficerMenu.show();
                            break;
                        case "STAFF" :
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
                LoginTracker.recordLogin(username, false, "Invalid credentials");
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
