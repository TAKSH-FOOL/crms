package com.crms.menu;

import com.crms.Session;
import com.crms.model.*;
import com.crms.dao.*;
import com.crms.util.InputHelper;
import com.crms.util.InputValidator;
import com.crms.util.PermissionChecker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminMenu {
    private static Scanner scanner = new Scanner(System.in);

    public static void show() {
        if (Session.getCurrentUser() == null || !"ADMIN".equals(Session.getCurrentUser().getRole())) {
            System.out.println("Access denied.");
            return;
        }
        int choice;
        if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {
            while (true) {
                System.out.println("\n========== SUPER ADMIN MENU ==========");
                System.out.println("1. Add Admin/Officer/Staff");
                System.out.println("2. Update User");
                System.out.println("3. Deactivate/Activate User");
                System.out.println("4. View All FIRs");
                System.out.println("5. View All Crime Records");
                System.out.println("6. View Audit Logs");
                System.out.println("7. View Login History");
                System.out.println("8. Generate Simple Reports");
                System.out.println("9. Add Police Station");
                System.out.println("10. View All Users");
                System.out.println("0. Logout");
                System.out.print("Enter choice: ");
                choice = InputHelper.readInt(scanner);

                switch (choice) {
                    case 1:
                        addUser();
                        break;
                    case 2:
                        updateUser();
                        break;
                    case 3:
                        toggleUserActive();
                        break;
                    case 4:
                        viewAllFIRs();
                        break;
                    case 5:
                        viewAllCrimes();
                        break;
                    case 6:
                        viewAuditLogs();
                        break;
                    case 7:
                        viewLoginHistory();
                        break;
                    case 8:
                        generateReports();
                        break;
                    case 9:
                        addPoliceStation();
                        break;
                    case 10:
                        viewAllUsers();
                        break;
                    case 0:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } else {
            while (true) {
                System.out.println("\n========== STATION ADMIN MENU ==========");
                System.out.println("1. Add Admin/Officer/Staff");
                System.out.println("2. Update User");
                System.out.println("3. Deactivate/Activate User");
                System.out.println("4. View FIRs (only within own station)");
                System.out.println("5. View All Crime Record");
                System.out.println("6. Generate Simple Reports (own station data)");
                System.out.println("7. Assign Officer to FIR (only officers from own station)");
                System.out.println("8. View All Users (only users from own station)");
                System.out.println("9. Logout");
                System.out.print("enter your choice : ");
                choice = InputHelper.readInt(scanner);
                switch (choice) {
                    case 1:
                        addUser();
                        break;
                    case 2:
                        updateUser();
                        break;
                    case 3:
                        toggleUserActive();
                        break;
                    case 4:
                        viewAllFIRs();
                        break;
                    case 5:
                        viewAllCrimes();
                        break;
                    case 6:
                        generateReports();
                        break;
                    case 7:
                        assignOfficerToFIR();
                        break;
                    case 8:
                        viewAllUsers();
                        break;
                    case 0:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        }
    }

    private static void addUser() {
        String username = InputHelper.username(scanner);
        String password = InputHelper.password(scanner);
        String role = InputHelper.role(scanner);
        String fullName = InputHelper.fullName(scanner);

        LinkedList<PoliceStation> stations = PoliceStationDAO.getAllActive();
        if (stations.isEmpty()) {
            System.out.println("No active police stations. Please add a station first.");
            return;
        }

        int idx = 0;
        int stationId = 0;
        if (PermissionChecker.isStationAdmin(Session.getCurrentUser())) {   // to check that the user is station admin or not
            stationId = Session.getCurrentUser().getStationId();           // if station admin then station id = admins station id
        } else {                                                             // if super admin then can have right to select police station
            System.out.println("Select station:");
            for (int i = 0; i < stations.size(); i++) {
                PoliceStation ps = stations.get(i);
                System.out.println((i + 1) + ". " + ps.getStationName());
            }
            idx = 0;
            while (true) {
                System.out.print("Station index : ");
                idx = InputHelper.readInt(scanner) - 1;
                if (idx < 0 || idx >= stations.size()) {
                    System.out.println("Invalid selection.");
                } else {
                    break;
                }
            }
            stationId = (stations.get(idx)).getId();
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setStationId(stationId);
        user.setFullName(fullName);
        user.setActive(true);
        if (UserDAO.create(user)) {           // first user creation
            if (user.getRole().equalsIgnoreCase("ADMIN")) {         //admin creation
                addAdmin(user.getId());
            } else if (user.getRole().equalsIgnoreCase("OFFICER")) {      //officer creation
                addOfficer(user.getId(), stationId);
            } else if (user.getRole().equalsIgnoreCase("STAFF")) {      //staff creation
                addStaff(user.getId(), user.getStationId());
            }
            System.out.println("User created with ID: " + user.getId());
        } else {
            System.out.println("Failed to create user. Username may already exist.");
        }
    }

    private static void addAdmin(int userId) {
        String designation = "";
        if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {  // if super admin then can assign super or station role
            designation = InputHelper.adminRoleByIndex(scanner);
        } else {
            designation = "Station Admin";             // station admin can create only station admin only
        }


        Admin admin = new Admin();
        admin.setUserId(userId);
        admin.setDesignation(designation);

        if (AdminDAO.create(admin)) {
            System.out.println("Admin record added.");
        } else {
            System.out.println("Failed to add admin record.");
        }
    }

    private static void updateUser() {

        String username = "";
        User user = null;
        if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {  // super admin can update all user
            viewAllUsers();
            username = InputHelper.username(scanner);
            user = UserDAO.getByUsername(username);
        } else {                                                  // station admin can update the users of his station only
            UserDAO.viewAllUserByStationId();
            username = InputHelper.userNameByStationId(scanner);
            if (UserDAO.isUserNameCurrentStationId(username)) {
                user = UserDAO.getByUsername(username);
            } else {
                System.out.println("user not found");
                return;
            }
        }
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        String userName = InputHelper.updateUsername(scanner);
        if (!userName.isEmpty()) user.setUsername(userName);

        String pwd = InputHelper.updatePassword(scanner);
        if (!pwd.isEmpty()) user.setPassword(pwd);

        String role = InputHelper.updateRole(scanner);
        if (!role.isEmpty()) user.setRole(role);

        String name = InputHelper.updateFullName(scanner);
        if (!name.isEmpty()) user.setFullName(name);

        int station_id = 0;
        if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {            // super admin can update the station id
            station_id = InputHelper.updateStationId(scanner);
            if (station_id != 0) user.setStationId(station_id);
        }

        if (UserDAO.update(user)) {
            System.out.println("User updated.");
        } else {
            System.out.println("Failed to update user.");
        }
    }

    private static boolean toggleUserActive() {
        String username = null;
        User user = null;

        if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {
            // Get all users, filter out the current Super Admin
            LinkedList<User> filteredUsers = UserDAO.getAll();
            int currentUserId = Session.getCurrentUser().getId();
            Iterator<User> iterator = filteredUsers.iterator();
            while (iterator.hasNext()) {
                User u = iterator.next();
                if (u.getId() == currentUserId) {
                    iterator.remove();
                }
            }
            if (filteredUsers.isEmpty()) {
                System.out.println("No other users to manage.");
                return false;
            }
            // Display filtered list
            displayUserTable(filteredUsers);
            username = InputHelper.userNameByIndex(scanner, filteredUsers);
            if (username != null) {
                user = UserDAO.getByUsername(username);
            }
        } else {
            // Station Admin logic – show users from own station (excluding admins)
            LinkedList<User> stationUsers = UserDAO.getAllUserByStationId();
            int currentUserId = Session.getCurrentUser().getId();
            Iterator<User> iterator = stationUsers.iterator();
            while (iterator.hasNext()) {
                User u = iterator.next();
                if (u.getId() == currentUserId || u.getRole().equals("ADMIN")) {
                    iterator.remove();
                }
            }
            if (stationUsers.isEmpty()) {
                System.out.println("No other users to manage.");
                return false;
            }
            // Display filtered list
            displayUserTable(stationUsers);
            username = InputHelper.userNameByIndex(scanner, stationUsers);
            if (username == null) {
                System.out.println("No users available.");
                return false;
            }
            if (UserDAO.isUserNameCurrentStationId(username) && UserDAO.isNonAdmin(username)) {
                user = UserDAO.getByUsername(username);
            } else {
                System.out.println("User not found or you have selected an admin.");
                return false;
            }
        }

        if (user == null) {
            System.out.println("User not found.");
            return false;
        }

        // Toggle for any other user
        user.setActive(!user.isActive());
        if (UserDAO.update(user)) {
            System.out.println("User active status toggled to: " + user.isActive());
        } else {
            System.out.println("Failed to update user.");
        }
        return false;
    }

    private static void addOfficer(int userId, int stationId) {
        String officerRole = "";
        while (true) {
            System.out.println("Enter Officer Post ");
            System.out.println("1. CONSTABLE");
            System.out.println("2. SUB_INSPECTOR");
            System.out.println("3. INSPECTOR");
            System.out.println("4. SUPERINTENDENT");
            System.out.print("enter index : ");
            int choice = InputHelper.readInt(scanner);
            if (choice == 1) {
                officerRole = "CONSTABLE";
                break;
            } else if (choice == 2) {
                officerRole = "SUB_INSPECTOR";
                break;
            } else if (choice == 3) {
                officerRole = "INSPECTOR";
                break;
            } else if (choice == 4) {
                officerRole = "SUPERINTENDENT";
                break;
            } else {
                System.out.println("Invalid officer rank.");
            }
        }

        Officer officer = new Officer();
        officer.setUserId(userId);
        officer.setOfficerRank(officerRole);
        officer.setStationId(stationId);
        if (OfficerDAO.create(officer)) {
            if (OfficerDAO.updateBadgeNumberAndEmail(officer)) {
                System.out.println("Officer added with Badge Number: " + officer.getBadgeNumber());
            } else {
                System.out.println("Officer added but badge/email update failed.");
            }
        } else {
            System.out.println("Failed to add officer. Badge number may be duplicate.");
        }
    }

    private static void addStaff(int userId, int stationId) {
        Staff staff = new Staff();
        staff.setUserId(userId);
        staff.setStationId(stationId);
        if (StaffDAO.create(staff)) {
            if (StaffDAO.updateEmployeeIdAndEmail(staff)) {
                System.out.println("Staff added with Employee ID: " + staff.getEmployeeId());
            } else {
                System.out.println("Staff added but employee ID/email update failed.");
            }
        } else {
            System.out.println("Failed to add staff. Employee ID may be duplicate.");
        }
    }

    private static boolean viewAllFIRs() {

        System.out.println("all active firs (y/n) : ");
        System.out.print("enter your choice  :");
        char choice = scanner.next().charAt(0);
        scanner.nextLine();
        if (choice == 'y' || choice == 'Y') {
            LinkedList<FIR> list = null;
            if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {
                list = FIRDAO.getAllActive();
            } else {
                list = FIRDAO.getByStationId(Session.getCurrentUser().getStationId());
            }

            if (list.isEmpty()) {
                System.out.println("No FIRs found.");
                return false;
            } else {
                for (int idx = 0; idx < list.size(); idx++) {
                    FIR f = list.get(idx);
                    System.out.println(f);
                }
            }
        } else {
            LinkedList<FIR> list = FIRDAO.getAllFir();
            if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {
                list = FIRDAO.getAllFir();
            } else {
                list = FIRDAO.getAllByStationId(Session.getCurrentUser().getStationId());
            }

            if (list.isEmpty()) {
                System.out.println("No FIRs found.");
                return false;
            }
            for (int idx = 0; idx < list.size(); idx++) {
                FIR f = list.get(idx);
                System.out.println(f);
            }
        }
        return true;
    }

    private static void viewAllCrimes() {
        LinkedList<CrimeRecord> list = null;
        if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {
            list = CrimeDAO.getAllActive();
        } else {
            list = CrimeDAO.getAllByStationId(Session.getCurrentUser().getStationId());
        }

        if (list.isEmpty()) {
            System.out.println("No crime records found.");
        } else {
            for (int i = 0; i < list.size(); i++) {
                CrimeRecord c = list.get(i);
                System.out.println(c);
            }
        }
    }

    private static void viewAuditLogs() {
        LinkedList<AuditLog> logs = AuditLogDAO.getAll();
        Collections.reverse(logs);
        if (logs.isEmpty()) {
            System.out.println("No audit logs.");
        } else {
            for (int i = 0; i < logs.size(); i++) {
                AuditLog log = logs.get(i);
                System.out.println(log);
                System.out.println();
            }
        }
    }

    private static void viewLoginHistory() {
        LinkedList<LoginHistory> history = LoginHistoryDAO.getAll();
        Collections.reverse(history);
        if (history.isEmpty()) {
            System.out.println("No login history.");
        } else {
            for (int i = 0; i < history.size(); i++) {
                LoginHistory h = history.get(i);
                System.out.println(h);
                System.out.println();

            }
        }
    }

    private static void generateReports() {
        System.out.println("\n===== REPORT GENERATOR =====");
        System.out.println("1. FIR Summary Report");
        System.out.println("2. Crime Record Summary Report");
        System.out.println("3. Officer Performance Report");
        if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {
            System.out.println("4. Station-wise Report (Super Admin only)");
        }

        System.out.print("Choose report type: ");
        int reportType = InputHelper.readInt(scanner);

        // Date range input
        LocalDate startDate = null, endDate = null;
        String startInput = "";
        while (true){
            System.out.print("Enter start date (dd-MM-yyyy) : ");
            startInput = scanner.nextLine().trim();
            if (!startInput.isEmpty()) {
                startDate = LocalDate.parse(startInput, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                if (InputValidator.isValidDate(startInput)) {
                    break;
                }

                else  {
                    System.out.println("Invalid date.");
                }
            }
            else  {
                System.out.println("Invalid date.");
            }
        }

        String endInput = "";
        while (true){
            System.out.print("Enter end date (dd-MM-yyyy) : ");
            endInput = scanner.nextLine().trim();
            if (!endInput.isEmpty()) {
                endDate = LocalDate.parse(endInput, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                if (startDate.isAfter(LocalDate.now())){
                    System.out.println("Invalid date.");
                    continue;
                }
                if (InputValidator.isValidDate(endInput)) {
                    break;
                }
                else   {
                    System.out.println("Invalid date.");
                }
            }
            else  {
                System.out.println("Invalid date.");
            }
        }


        // Station filter
        int stationId = 0; // 0 means all stations
        if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {
            System.out.print("Enter station ID (0 for all stations): ");
            stationId = InputHelper.readInt(scanner);
        } else {
            stationId = Session.getCurrentUser().getStationId();
        }

        if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {
            switch (reportType) {
                case 1 -> generateFIRSummaryReport(startDate, endDate, stationId);
                case 2 -> generateCrimeSummaryReport(startDate, endDate, stationId);
                case 3 -> generateOfficerPerformanceReport(startDate, endDate, stationId);
                case 4 -> {
                    if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {
                        generateStationWiseReport(startDate, endDate);
                    } else {
                        System.out.println("Access denied. This report is only for Super Admin.");
                    }
                }
                default -> System.out.println("Invalid choice.");
            }
        }

        else {
            switch (reportType) {
                case 1 -> generateFIRSummaryReport(startDate, endDate, stationId);
                case 2 -> generateCrimeSummaryReport(startDate, endDate, stationId);
                case 3 -> generateOfficerPerformanceReport(startDate, endDate, stationId);
                default -> System.out.println("Invalid choice.");
            }
        }

    }

    // Add Police Station
    private static void addPoliceStation() {
        String name = "";
        while (true) {
            System.out.print("Station Name: ");
            name = scanner.nextLine();
            if (PoliceStationDAO.isStationNameAvailable(name)) {
                break;
            } else {
                System.out.println("Station name is already in use.");
            }
        }
        String addr = InputHelper.address(scanner);

        String phone = InputHelper.phone(scanner);

        PoliceStation station = new PoliceStation();
        station.setStationName(name);
        station.setAddress(addr);
        station.setPhone(phone);
        station.setStationCode(PoliceStationDAO.stationCodeGenerator(name));
        station.setActive(true);
        if (PoliceStationDAO.create(station)) {
            System.out.println("Police station added with ID: " + station.getId());
        } else {
            System.out.println("Failed to add police station. Name may already exist.");
        }
    }

    // NEW: Assign Officer to FIR
    private static void assignOfficerToFIR() {
        if (FIRDAO.getAllInAssignedFIRByStation(Session.getCurrentUser().getStationId()).isEmpty()) {
            System.out.println("no fir currently available");
            return;
        }

        String firNum = "";

        // Only allow assignment for FIRs in the user's station
        FIRDAO.displayAllInAssignedFIRByStation(Session.getCurrentUser().getStationId());
        if (FIRDAO.getAllInAssignedFIRByStation(Session.getCurrentUser().getStationId()).isEmpty()) {
            System.out.println("No unassigned FIRs available for assignment.");
            return;
        }
        while (true) {
            firNum = InputHelper.FIRNumber(scanner);
            if (!FIRDAO.isFIRAtStation(firNum, Session.getCurrentUser().getStationId())) {
                System.out.println("Selected FIR is not at your station.");
            }
            else {
                break;
            }
        }


        FIR fir = FIRDAO.getByFIRNumber(firNum);
        if (fir == null || !fir.isActive()) {
            System.out.println("FIR not found or inactive.");
            return;
        }

        LinkedList<Officer> officers = OfficerDAO.getUnassignedOfficersByStation(fir.getStationId());
        if (officers.isEmpty()) {
            System.out.println("No officers available.");
            return;
        }
        System.out.println("Select officer:");
        for (int i = 0; i < officers.size(); i++) {
            User u = UserDAO.getById(officers.get(i).getUserId());
            System.out.println((i + 1) + ". " + (u != null ? u.getFullName() : "Unknown") +
                    " (Badge: " + officers.get(i).getBadgeNumber() + ")");
        }
        int idx;
        while (true) {
            System.out.print("enter the index : ");
            idx = InputHelper.readInt(scanner) - 1;
            if (idx < 0 || idx >= officers.size()) {
                System.out.println("Invalid selection.");
            } else {
                break;
            }
        }


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

    private static void viewAllUsers() {
        System.out.println("1. Active Users");
        System.out.println("2. All Users");
        System.out.println("3. User By Role");
        System.out.print("Choose an option: ");
        int choice = InputHelper.readInt(scanner);
        LinkedList<User> users;
        switch (choice) {
            case 1:
                if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {
                    users = UserDAO.getAllActive();
                } else {
                    users = UserDAO.getUsersByStation(Session.getCurrentUser().getStationId(), null, true);
                }
                break;
            case 2:
                if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {
                    users = UserDAO.getAll();
                } else {
                    users = UserDAO.getUsersByStation(Session.getCurrentUser().getStationId(), null, false);
                }
                break;
            case 3:
                System.out.print("Enter role: ");
                String role = scanner.nextLine();
                if (PermissionChecker.isSuperAdmin(Session.getCurrentUser())) {
                    users = UserDAO.searchByRole(role);
                } else {
                    users = UserDAO.getUsersByStation(Session.getCurrentUser().getStationId(), role, false);
                }
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        // Build a map of station ID -> station code for quick lookup
        LinkedList<PoliceStation> stations = PoliceStationDAO.getAllActive();
        Map<Integer, String> stationCodeMap = new HashMap<>();
        for (int i = 0; i < stations.size(); i++) {
            PoliceStation ps = stations.get(i);
            stationCodeMap.put(ps.getId(), ps.getStationCode());
        }

        System.out.println("\n+------+-----------------+--------------------------------+----------+----------------------+----------+--------+");
        System.out.printf("| %-4s | %-15s | %-30s | %-8s | %-20s | %-8s | %-6s |\n",
                "ID", "Username", "Email", "Role", "Full Name", "Station", "Active");
        System.out.println("+------+-----------------+--------------------------------+----------+----------------------+----------+--------+");

        for (User u : users) {
            String stationCode = stationCodeMap.getOrDefault(u.getStationId(), "N/A");
            String email = u.getEmail() != null ? u.getEmail() : "N/A";
            // Truncate email to 30 chars if longer
            if (email.length() > 30) email = email.substring(0, 27) + "...";
            String active = u.isActive() ? "Yes" : "No";

            System.out.printf("| %-4d | %-15s | %-30s | %-8s | %-20s | %-8s | %-6s |\n",
                    u.getId(),
                    u.getUsername(),
                    email,
                    u.getRole(),
                    u.getFullName(),
                    stationCode,
                    active);
        }
        System.out.println("+------+-----------------+--------------------------------+----------+----------------------+----------+--------+");
    }

     static LinkedList<FIR> filterFIRsByDate(LinkedList<FIR> list, LocalDate start, LocalDate end) {
        if (start == null && end == null) return list;
        LinkedList<FIR> filtered = new LinkedList<>();
        for (FIR f : list) {
            LocalDate incidentDate = f.getIncidentDate().toLocalDate();
            if (start != null && incidentDate.isBefore(start)) continue;
            if (end != null && incidentDate.isAfter(end)) continue;
            filtered.add(f);
        }
        return filtered;
    }

     static LinkedList<CrimeRecord> filterCrimesByDate(LinkedList<CrimeRecord> list, LocalDate start, LocalDate end) {
        if (start == null && end == null) return list;
        LinkedList<CrimeRecord> filtered = new LinkedList<>();
        for (CrimeRecord c : list) {
            LocalDate incidentDate = c.getIncidentDate().toLocalDate();
            if (start != null && incidentDate.isBefore(start)) continue;
            if (end != null && incidentDate.isAfter(end)) continue;
            filtered.add(c);
        }
        return filtered;
    }

     static void generateFIRSummaryReport(LocalDate start, LocalDate end, int stationId) {
        LinkedList<FIR> allFIRs;
        if (stationId == 0) {
            allFIRs = FIRDAO.getAllFir(); // includes all stations
        } else {
            allFIRs = FIRDAO.getAllByStationId(stationId);
        }
        LinkedList<FIR> filtered = filterFIRsByDate(allFIRs, start, end);

        int filed = 0, assigned = 0, investigating = 0, closed = 0;
        for (FIR f : filtered) {
            switch (f.getStatus()) {
                case "FILED" -> filed++;
                case "ASSIGNED" -> assigned++;
                case "INVESTIGATING" -> investigating++;
                case "CLOSED" -> closed++;
            }
        }
        System.out.println("\n========== FIR SUMMARY REPORT ==========");
        System.out.println("Period: " + (start != null ? start : "All") + " to " + (end != null ? end : "All"));
        System.out.println("Station: " + (stationId == 0 ? "All" : PoliceStationDAO.getStationNameById(stationId)));
        System.out.println("Total FIRs: " + filtered.size());
        System.out.println("  FILED         : " + filed);
        System.out.println("  ASSIGNED      : " + assigned);
        System.out.println("  INVESTIGATING : " + investigating);
        System.out.println("  CLOSED        : " + closed);
        System.out.println("========================================\n");
    }

     static void generateCrimeSummaryReport(LocalDate start, LocalDate end, int stationId) {
        LinkedList<CrimeRecord> allCrimes;
        if (stationId == 0) {
            allCrimes = CrimeDAO.getAllCrimeRecords();
        } else {
            allCrimes = CrimeDAO.getAllByStationId(stationId);
        }
        LinkedList<CrimeRecord> filtered = filterCrimesByDate(allCrimes, start, end);

        int active = 0, solved = 0, closed = 0;
        for (CrimeRecord c : filtered) {
            switch (c.getStatus()) {
                case "ACTIVE" -> active++;
                case "SOLVED" -> solved++;
                case "CLOSED" -> closed++;
            }
        }
        System.out.println("\n========== CRIME SUMMARY REPORT ==========");
        System.out.println("Period: " + (start != null ? start : "All") + " to " + (end != null ? end : "All"));
        System.out.println("Station: " + (stationId == 0 ? "All" : PoliceStationDAO.getStationNameById(stationId)));
        System.out.println("Total Crime Records: " + filtered.size());
        System.out.println("  ACTIVE  : " + active);
        System.out.println("  SOLVED  : " + solved);
        System.out.println("  CLOSED  : " + closed);
        System.out.println("===========================================\n");
    }

     static void generateOfficerPerformanceReport(LocalDate start, LocalDate end, int stationId) {
        LinkedList<Officer> officers;
        if (stationId == 0) {
            officers = OfficerDAO.getAll(); // you may need to add this method
        } else {
            officers = OfficerDAO.getAllByStationId(stationId);
        }
        if (officers.isEmpty()) {
            System.out.println("No officers found.");
            return;
        }
        System.out.println("\n========== OFFICER PERFORMANCE REPORT ==========");
        System.out.println("Period: " + (start != null ? start : "All") + " to " + (end != null ? end : "All"));
        System.out.println("Station: " + (stationId == 0 ? "All" : PoliceStationDAO.getStationNameById(stationId)));
        System.out.println("-------------------------------------------------");
        System.out.printf("%-10s %-20s %-15s %-15s\n", "Badge", "Officer Name", "Assigned", "Closed");
        System.out.println("-------------------------------------------------");
        for (Officer off : officers) {
            User u = UserDAO.getById(off.getUserId());
            String name = u != null ? u.getFullName() : "Unknown";
            LinkedList<FIR> assignedFIRs = FIRDAO.getByOfficerId(off.getId());
            int totalAssigned = assignedFIRs.size();
            int closedCount = 0;
            for (FIR f : assignedFIRs) {
                if ("CLOSED".equals(f.getStatus())) closedCount++;
            }
            System.out.printf("%-10s %-20s %-15d %-15d\n", off.getBadgeNumber(), name, totalAssigned, closedCount);
        }
        System.out.println("===================================================\n");
    }

    static void generateStationWiseReport(LocalDate start, LocalDate end) {
        LinkedList<PoliceStation> stations = PoliceStationDAO.getAllActive();
        System.out.println("\n========== STATION-WISE FIR REPORT ==========");
        System.out.println("Period: " + (start != null ? start : "All") + " to " + (end != null ? end : "All"));
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%-5s %-20s %-10s %-10s %-10s %-15s %-10s\n",
                "ID", "Station Name", "Total", "Filed", "Assigned", "Investigating", "Closed");
        System.out.println("-----------------------------------------------------------------------------");
        for (PoliceStation ps : stations) {
            LinkedList<FIR> stationFIRs = FIRDAO.getAllByStationId(ps.getId());
            LinkedList<FIR> filtered = filterFIRsByDate(stationFIRs, start, end);
            int filed = 0, assigned = 0, investigating = 0, closed = 0;
            for (FIR f : filtered) {
                switch (f.getStatus()) {
                    case "FILED" -> filed++;
                    case "ASSIGNED" -> assigned++;
                    case "INVESTIGATING" -> investigating++;
                    case "CLOSED" -> closed++;
                }
            }
            System.out.printf("%-5d %-20s %-10d %-10d %-10d %-15d %-10d\n",
                    ps.getId(),
                    ps.getStationName(),
                    filtered.size(),
                    filed,
                    assigned,
                    investigating,
                    closed);
        }
        System.out.println("-----------------------------------------------------------------------------\n");
    }

    private static void displayUserTable(LinkedList<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users to display.");
            return;
        }

        LinkedList<PoliceStation> stations = PoliceStationDAO.getAllActive();
        Map<Integer, String> stationCodeMap = new HashMap<>();
        for (PoliceStation ps : stations) {
            stationCodeMap.put(ps.getId(), ps.getStationCode());
        }

        System.out.println("\n+------+------+-----------------+--------------------------------+----------+----------------------+----------+--------+");
        System.out.printf("| %-4s | %-4s | %-15s | %-30s | %-8s | %-20s | %-8s | %-6s |\n",
                "S.No", "ID", "Username", "Email", "Role", "Full Name", "Station", "Active");
        System.out.println("+------+------+-----------------+--------------------------------+----------+----------------------+----------+--------+");

        int serial = 1;
        for (User u : users) {
            String stationCode = stationCodeMap.getOrDefault(u.getStationId(), "N/A");
            String email = u.getEmail() != null ? u.getEmail() : "N/A";
            if (email.length() > 30) email = email.substring(0, 27) + "...";
            String active = u.isActive() ? "Yes" : "No";

            System.out.printf("| %-4d | %-4d | %-15s | %-30s | %-8s | %-20s | %-8s | %-6s |\n",
                    serial++,
                    u.getId(),
                    u.getUsername(),
                    email,
                    u.getRole(),
                    u.getFullName(),
                    stationCode,
                    active);
        }
        System.out.println("+------+------+-----------------+--------------------------------+----------+----------------------+----------+--------+");
    }
}