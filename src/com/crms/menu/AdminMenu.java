package com.crms.menu;

import com.crms.Session;
import com.crms.model.*;
import com.crms.dao.*;
import com.crms.util.InputHelper;
import com.crms.util.InputValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private static Scanner scanner = new Scanner(System.in);

    public static void show() {
        if (Session.getCurrentUser() == null || !"ADMIN".equals(Session.getCurrentUser().getRole())) {
            System.out.println("Access denied.");
            return;
        }
        int choice;
        do {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. Add User");
            System.out.println("2. Update User");
            System.out.println("3. Deactivate/Activate User");
            System.out.println("4. Add Officer");
            System.out.println("5. Add Staff");
            System.out.println("6. View All FIRs");
            System.out.println("7. View All Crime Records");
            System.out.println("8. View Audit Logs");
            System.out.println("9. View Login History");
            System.out.println("10. Generate Simple Reports");
            System.out.println("11. Add Police Station");          // NEW
            System.out.println("12. Assign Officer to FIR");       // NEW
            System.out.println("0. Logout");
            choice = InputHelper.readInt(scanner, "Enter choice: ");

            switch (choice) {
                case 1: addUser(); break;
                case 2: updateUser(); break;
                case 3: toggleUserActive(); break;
                case 4: addOfficer(); break;
                case 5: addStaff(); break;
                case 6: viewAllFIRs(); break;
                case 7: viewAllCrimes(); break;
                case 8: viewAuditLogs(); break;
                case 9: viewLoginHistory(); break;
                case 10: generateReports(); break;
                case 11: addPoliceStation(); break;
                case 12: assignOfficerToFIR(); break;
                case 0: System.out.println("Logging out..."); return;
                default: System.out.println("Invalid choice.");
            }
        } while (true);
    }

    private static void addUser() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }
        System.out.print("Password: ");
        String password = scanner.nextLine();
        if (password.trim().isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }
        System.out.print("Role (ADMIN/OFFICER/STAFF): ");
        String role = scanner.nextLine().toUpperCase();
        if (!role.matches("ADMIN|OFFICER|STAFF")) {
            System.out.println("Invalid role. Must be ADMIN, OFFICER, or STAFF.");
            return;
        }
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine().trim();
        if (fullName.isEmpty()) {
            System.out.println("Full name cannot be empty.");
            return;
        }
        List<PoliceStation> stations = PoliceStationDAO.getAllActive();
        if (stations.isEmpty()) {
            System.out.println("No active police stations. Please add a station first.");
            return;
        }
        System.out.println("Select station:");
        for (int i = 0; i < stations.size(); i++) {
            System.out.println((i + 1) + ". " + stations.get(i).getStationName());
        }
        int idx = InputHelper.readInt(scanner, "Station number: ") - 1;
        if (idx < 0 || idx >= stations.size()) { System.out.println("Invalid selection."); return; }
        int stationId = stations.get(idx).getId();

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setStationId(stationId);
        user.setFullName(fullName);
        user.setActive(true);
        if (UserDAO.create(user)) {
            System.out.println("User created with ID: " + user.getId());
        } else {
            System.out.println("Failed to create user. Username may already exist.");
        }
    }

    private static void updateUser() {
        System.out.print("Enter username of user to update: ");
        String username = scanner.nextLine();
        User user = UserDAO.getByUsername(username);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        System.out.print("New password (leave blank to keep): ");
        String pwd = scanner.nextLine();
        if (!pwd.isEmpty()) user.setPassword(pwd);
        System.out.print("New role (ADMIN/OFFICER/STAFF, leave blank to keep): ");
        String role = scanner.nextLine().toUpperCase();
        if (!role.isEmpty() && role.matches("ADMIN|OFFICER|STAFF")) user.setRole(role);
        else if (!role.isEmpty()) System.out.println("Invalid role, keeping old.");
        System.out.print("New full name (leave blank to keep): ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) user.setFullName(name);
        // Station update optional – omitted for simplicity
        if (UserDAO.update(user)) {
            System.out.println("User updated.");
        } else {
            System.out.println("Failed to update user.");
        }
    }

    private static void toggleUserActive() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        User user = UserDAO.getByUsername(username);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        user.setActive(!user.isActive());
        if (UserDAO.update(user)) {
            System.out.println("User active status toggled to: " + user.isActive());
        } else {
            System.out.println("Failed to update user.");
        }
    }

    private static void addOfficer() {
        System.out.print("Enter user ID for the officer: ");
        int userId = InputHelper.readInt(scanner, "User ID: ");
        User user = UserDAO.getById(userId);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        if (!"OFFICER".equals(user.getRole())) {
            System.out.println("User role must be OFFICER.");
            return;
        }
        System.out.print("Badge Number: ");
        String badge = scanner.nextLine();
        System.out.print("Officer Rank: ");
        String rank = scanner.nextLine();
        Officer officer = new Officer();
        officer.setUserId(userId);
        officer.setBadgeNumber(badge);
        officer.setOfficerRank(rank);
        if (OfficerDAO.create(officer)) {
            System.out.println("Officer added.");
        } else {
            System.out.println("Failed to add officer. Badge number may be duplicate.");
        }
    }

    private static void addStaff() {
        System.out.print("Enter user ID for the staff: ");
        int userId = InputHelper.readInt(scanner, "User ID: ");
        User user = UserDAO.getById(userId);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        if (!"STAFF".equals(user.getRole())) {
            System.out.println("User role must be STAFF.");
            return;
        }
        System.out.print("Employee ID: ");
        String empId = scanner.nextLine();
        Staff staff = new Staff();
        staff.setUserId(userId);
        staff.setEmployeeId(empId);
        if (StaffDAO.create(staff)) {
            System.out.println("Staff added.");
        } else {
            System.out.println("Failed to add staff. Employee ID may be duplicate.");
        }
    }

    private static void viewAllFIRs() {
        List<FIR> list = FIRDAO.getAllActive();
        if (list.isEmpty()) {
            System.out.println("No FIRs found.");
        } else {
            for (FIR f : list) System.out.println(f);
        }
    }

    private static void viewAllCrimes() {
        List<CrimeRecord> list = CrimeDAO.getAllActive();
        if (list.isEmpty()) {
            System.out.println("No crime records found.");
        } else {
            for (CrimeRecord c : list) System.out.println(c);
        }
    }

    private static void viewAuditLogs() {
        List<AuditLog> logs = AuditLogDAO.getAll();
        if (logs.isEmpty()) {
            System.out.println("No audit logs.");
        } else {
            for (AuditLog log : logs) System.out.println(log);
        }
    }

    private static void viewLoginHistory() {
        List<LoginHistory> history = LoginHistoryDAO.getAll();
        if (history.isEmpty()) {
            System.out.println("No login history.");
        } else {
            for (LoginHistory h : history) System.out.println(h);
        }
    }

    private static void generateReports() {
        List<FIR> firs = FIRDAO.getAllActive();
        long filed = firs.stream().filter(f -> "FILED".equals(f.getStatus())).count();
        long assigned = firs.stream().filter(f -> "ASSIGNED".equals(f.getStatus())).count();
        long investigating = firs.stream().filter(f -> "INVESTIGATING".equals(f.getStatus())).count();
        long closed = firs.stream().filter(f -> "CLOSED".equals(f.getStatus())).count();
        System.out.println("=== Report ===");
        System.out.println("Total FIRs: " + firs.size());
        System.out.println("  FILED: " + filed);
        System.out.println("  ASSIGNED: " + assigned);
        System.out.println("  INVESTIGATING: " + investigating);
        System.out.println("  CLOSED: " + closed);

        List<CrimeRecord> crimes = CrimeDAO.getAllActive();
        long active = crimes.stream().filter(c -> "ACTIVE".equals(c.getStatus())).count();
        long solved = crimes.stream().filter(c -> "SOLVED".equals(c.getStatus())).count();
        long closedCrime = crimes.stream().filter(c -> "CLOSED".equals(c.getStatus())).count();
        System.out.println("Crime Records: " + crimes.size());
        System.out.println("  ACTIVE: " + active);
        System.out.println("  SOLVED: " + solved);
        System.out.println("  CLOSED: " + closedCrime);
    }

    // NEW: Add Police Station
    private static void addPoliceStation() {
        System.out.print("Station Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Station name cannot be empty.");
            return;
        }
        System.out.print("Address: ");
        String addr = scanner.nextLine().trim();
        if (addr.isEmpty()) {
            System.out.println("Address cannot be empty.");
            return;
        }
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        if (!InputValidator.isValidPhone(phone)) {
            System.out.println("Invalid phone number.");
            return;
        }
        PoliceStation station = new PoliceStation();
        station.setStationName(name);
        station.setAddress(addr);
        station.setPhone(phone);
        station.setActive(true);
        if (PoliceStationDAO.create(station)) {
            System.out.println("Police station added with ID: " + station.getId());
        } else {
            System.out.println("Failed to add police station. Name may already exist.");
        }
    }

    // NEW: Assign Officer to FIR
    private static void assignOfficerToFIR() {
        System.out.print("Enter FIR number: ");
        String firNum = scanner.nextLine();
        FIR fir = FIRDAO.getByFIRNumber(firNum);
        if (fir == null || !fir.isActive()) {
            System.out.println("FIR not found or inactive.");
            return;
        }
        List<Officer> officers = OfficerDAO.getAll();
        if (officers.isEmpty()) {
            System.out.println("No officers available.");
            return;
        }
        System.out.println("Select officer:");
        for (int i = 0; i < officers.size(); i++) {
            User u = UserDAO.getById(officers.get(i).getUserId());
            System.out.println((i+1) + ". " + (u != null ? u.getFullName() : "Unknown") +
                    " (Badge: " + officers.get(i).getBadgeNumber() + ")");
        }
        int idx = InputHelper.readInt(scanner, "Officer number: ") - 1;
        if (idx < 0 || idx >= officers.size()) { System.out.println("Invalid selection."); return; }
        int officerId = officers.get(idx).getId();
        fir.setAssignedOfficerId(officerId);
        // Also update status to ASSIGNED if currently FILED
        if ("FILED".equals(fir.getStatus())) {
            fir.setStatus("ASSIGNED");
        }
        if (FIRDAO.update(fir)) {
            System.out.println("Officer assigned to FIR.");
        } else {
            System.out.println("Failed to assign officer.");
        }
    }
}