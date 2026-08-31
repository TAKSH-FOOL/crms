package com.crms.util;

import com.crms.Session;
import com.crms.dao.*;
import com.crms.model.CrimeCategory;
import com.crms.model.FIR;
import com.crms.model.Officer;
import com.crms.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Scanner;

public class InputHelper {
    public static int readInt(Scanner scanner) {
        while (true) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
                System.out.print("enter : ");
            }
        }
    }


    public static String FIRNumber(Scanner scanner) {
        while (true) {
            System.out.print("Enter FIR Number (FIR-<stationCode>-<serialNumber>): ");
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                if (InputValidator.isValidFIRNumber(line)) {
                    return line;
                } else {
                    System.out.println("Please enter a valid FIR number.");
                }
            } else {
                System.out.println("Please enter a valid FIR number.");
            }
        }
    }

    public static String CrimeNumber(Scanner scanner) {
        while (true) {
            System.out.print("Enter Crime Number (CN-<firnumber>-<serialNumber>,leave blank): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line;
            }
            else if (InputValidator.isValidCrimeNumber(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid Crime number.");
            }
        }
    }


    public static String FIRStatus(Scanner scanner) {
        while (true) {
            System.out.print("Enter FIR Status (FILED/ASSIGNED/INVESTIGATING/CLOSED): ");
            String line = scanner.nextLine().trim().toUpperCase();
            if (line.matches("FILED|ASSIGNED|INVESTIGATING|CLOSED")) {
                return line;
            } else {
                System.out.println("Please enter a valid FIR status.");
            }
        }
    }

    public static String userNameByIndex(Scanner scanner, LinkedList<User> userList) {
        if (userList == null || userList.isEmpty()) {
            System.out.println("No users available.");
            return null;
        }

        while (true) {
            System.out.print("Enter user index (1-" + userList.size() + "): ");
            int idx = InputHelper.readInt(scanner) - 1;
            if (idx >= 0 && idx < userList.size()) {
                return userList.get(idx).getUsername();
            } else {
                System.out.println("Invalid selection. Please enter a number between 1 and " + userList.size() + ".");
            }
        }
    }


    public static String harmDescription(Scanner scanner) {
        while (true) {
            System.out.print("Enter Harm Description: ");
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            } else {
                System.out.println("Harm description cannot be empty.");
            }
        }
    }

    public static String firstName(Scanner scanner) {
        while (true) {
            System.out.print("Enter first name: ");
            String line = scanner.nextLine().trim();
            if (InputValidator.isValidName(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid name.");
            }
        }
    }

    public static String lastName(Scanner scanner) {
        while (true) {
            System.out.print("Enter Last Name: ");
            String line = scanner.nextLine().trim();
            if (InputValidator.isValidName(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid name.");
            }
        }
    }

    public static String updateFirstName(Scanner scanner) {
        while (true) {
            System.out.print("Enter First Name (leave blank to keep): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line;
            } else if (InputValidator.isValidName(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid name.");
            }
        }
    }

    public static String updateLastName(Scanner scanner) {
        while (true) {
            System.out.print("Enter Last Name (leave blank to keep): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line;
            } else if (InputValidator.isValidName(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid name.");
            }
        }
    }


    public static String dob(Scanner scanner) {
        while (true) {
            System.out.print("Enter date of birth (YYYY-MM-DD): ");
            String line = scanner.nextLine().trim();
            if (InputValidator.isValidDate(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid date of birth.");
            }
        }
    }

    public static String updateDob(Scanner scanner) {
        while (true) {
            System.out.print("Enter date of birth (YYYY-MM-DD, leave blank to keep): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line;
            } else if (InputValidator.isValidDate(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid date of birth.");
            }
        }
    }

    public static String gender(Scanner scanner) {
        while (true) {
            System.out.print("Enter gender (MALE/FEMALE/OTHER): ");
            String line = scanner.nextLine().trim().toUpperCase();
            if (InputValidator.isValidGender(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid gender.");
            }
        }
    }

    public static String updateGender(Scanner scanner) {
        while (true) {
            System.out.print("Enter gender (MALE/FEMALE/OTHER, leave blank to keep): ");
            String line = scanner.nextLine().trim().toUpperCase();
            if (line.isEmpty()) {
                return line;
            } else if (InputValidator.isValidGender(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid gender.");
            }
        }
    }

    public static String address(Scanner scanner) {
        while (true) {
            System.out.print("Enter address: ");
            String line = scanner.nextLine().trim();
            if (InputValidator.isValidAddress(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid address.");
            }
        }
    }

    public static String updateAddress(Scanner scanner) {
        while (true) {
            System.out.print("Enter address (leave blank to keep): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line;
            } else if (InputValidator.isValidAddress(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid address.");
            }
        }
    }

    public static String phone(Scanner scanner) {
        while (true) {
            System.out.print("Enter phone number: ");
            String line = scanner.nextLine().trim();
            if (InputValidator.isValidPhone(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid phone number.");
            }
        }
    }

    public static String updatePhone(Scanner scanner) {
        while (true) {
            System.out.print("Enter phone number (leave blank to keep): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line;
            } else if (InputValidator.isValidPhone(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid phone number.");
            }
        }
    }

    public static String wantedStatus(Scanner scanner) {
        while (true) {
            System.out.print("Enter wanted status (WANTED/NOT_WANTED): ");
            String line = scanner.nextLine().trim().toUpperCase();
            if (line.matches("WANTED|NOT_WANTED")) {
                return line;
            } else {
                System.out.println("Please enter a valid wanted status.");
            }
        }
    }

    public static String updateWantedStatus(Scanner scanner) {
        while (true) {
            System.out.print("Enter wanted status (WANTED/NOT_WANTED) (leave blank to keep): ");
            String line = scanner.nextLine().trim().toUpperCase();
            if (line.isEmpty()) {
                return line;
            } else if (line.matches("WANTED|NOT_WANTED")) {
                return line;
            } else {
                System.out.println("Please enter a valid wanted status.");
            }
        }
    }

    public static String criminalRole(Scanner scanner) {
        while (true) {
            System.out.print("Enter criminal role (PERPETRATOR/ACCOMPLICE): ");
            String line = scanner.nextLine().trim().toUpperCase();
            if (line.matches("PERPETRATOR|ACCOMPLICE")) {
                return line;
            } else {
                System.out.println("Please enter a valid criminal role.");
            }
        }
    }

    public static String evidenceType(Scanner scanner) {
        while (true) {
            System.out.print("Type (DOCUMENT/IMAGE/PHYSICAL/etc): ");
            String line = scanner.nextLine().toUpperCase();

            if (line.matches("DOCUMENT|IMAGE|PHYSICAL")) {
                return line;
            } else if (line.isEmpty()) {
                return line;
            } else {
                System.out.println("Please enter a valid evidence type.");
            }
        }
    }


    // ---------- Crime Name ----------
    public static String crimeName(Scanner scanner) {
        while (true) {
            System.out.print("Enter Crime Name: ");
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            } else {
                System.out.println("Crime name cannot be empty.");
            }
        }
    }

    // ---------- Crime Description ----------
    public static String crimeDescription(Scanner scanner) {
        while (true) {
            System.out.print("Enter Crime Description: ");
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            } else {
                System.out.println("Description cannot be empty.");
            }
        }
    }

    // ---------- Crime Location ----------
    public static String crimeLocation(Scanner scanner) {
        while (true) {
            System.out.print("Enter Crime Location: ");
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            } else {
                System.out.println("Location cannot be empty.");
            }
        }
    }

    public static LocalDateTime incidentDateTime(Scanner scanner) {
        while (true) {
            System.out.print("Enter Incident Date and Time (dd-MM-yyyy HH:mm): ");
            String line = scanner.nextLine().trim();
            if (InputValidator.isValidDateTime(line)) {
                return LocalDateTime.parse(line, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            } else {
                System.out.println("Invalid format. Please used dd-MM-yyyy HH:mm");
            }
        }
    }


    public static String date(Scanner scanner) {
        while (true) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line; // skip
            }
            if (InputValidator.isValidDate(line)) {
                return line;
            } else {
                System.out.print("Invalid date. Use dd-MM-yyyy (or press Enter to skip): ");
            }
        }
    }


    public static String getFirNumbersByOfficerId(Scanner scanner) {
        Officer officer = OfficerDAO.getByUserId(Session.getCurrentUser().getId());
        FIRDAO.displayFIRNumbersByOfficerId(officer.getId());
        LinkedList<FIR> list = FIRDAO.getByOfficerId(officer.getId());
        if (list.isEmpty()){
            return null;
        }
        while (true) {
            System.out.print("Enter FIR Index: ");
            int idx = InputHelper.readInt(scanner) - 1;
            if (idx >= 0 && idx < list.size()) {
                FIR fir = list.get(idx);
                return fir.getFirNumber();
            } else {
                System.out.println("Invalid FIR index.");
            }
        }
    }

    public static String crimeNumberOrNull(Scanner scanner) {
        System.out.print("Enter Crime Number (or press Enter to leave blank): ");
        String line = scanner.nextLine().trim();
        return line.isEmpty() ? null : line;
    }


    // ---------- Crime Status ----------
    public static String crimeStatus(Scanner scanner) {
        while (true) {
            System.out.print("Enter Crime Status (ACTIVE/SOLVED/CLOSED): ");
            String line = scanner.nextLine().trim().toUpperCase();
            if (line.matches("ACTIVE|SOLVED|CLOSED")) {
                return line;
            } else {
                System.out.println("Please enter a valid status (ACTIVE/SOLVED/CLOSED).");
            }
        }
    }

    public static String complainantName(Scanner scanner) {
        while (true) {
            System.out.print("Enter Complainant Name: ");
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            } else {
                System.out.println("Complainant name cannot be empty.");
            }
        }
    }

    public static String updateComplainantName(Scanner scanner) {
        while (true) {
            System.out.print("New Complainant Name (leave blank to keep): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line;
            } else {
                return line; // any non‑empty is accepted
            }
        }
    }

    // ---------- Complainant Contact ----------
    public static String complainantContact(Scanner scanner) {
        while (true) {
            System.out.print("Enter Complainant Contact: ");
            String line = scanner.nextLine().trim();
            // Basic validation: non‑empty and contains only digits, spaces, +, -
            if (InputValidator.isValidPhone(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid contact number .");
            }
        }
    }

    public static String updateComplainantContact(Scanner scanner) {
        while (true) {
            System.out.print("New Complainant Contact (leave blank to keep): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line;
            } else if (InputValidator.isValidPhone(line)) {
                return line;
            } else {
                System.out.println("Please enter a valid contact number.");
            }
        }
    }

    // ---------- Incident Location ----------
    public static String incidentLocation(Scanner scanner) {
        while (true) {
            System.out.print("Enter Incident Location: ");
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            } else {
                System.out.println("Incident location cannot be empty.");
            }
        }
    }

    public static String updateIncidentLocation(Scanner scanner) {
        while (true) {
            System.out.print(" Incident Location (leave blank to keep): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line;
            } else {
                return line;
            }
        }
    }

    // ---------- Incident Description ----------
    public static String incidentDescription(Scanner scanner) {
        while (true) {
            System.out.print("Enter Incident Description: ");
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            } else {
                System.out.println("Incident description cannot be empty.");
            }
        }
    }

    public static String updateIncidentDescription(Scanner scanner) {
        while (true) {
            System.out.print("New Incident Description (leave blank to keep): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line;
            } else {
                return line;
            }
        }
    }

    public static String crimeCategoryIndexName(Scanner scanner) {
        CrimeCategoryDAO.viewAllCrimeCategory();
        LinkedList<CrimeCategory> list = CrimeCategoryDAO.getAllCategory();
        if (list != null) {
            while (true) {
                System.out.print("Enter Crime Category Index : ");
                int idx = scanner.nextInt() - 1;
                if (idx >= 0 && idx < list.size()) {
                    CrimeCategory cc = list.get(idx);
                    return cc.getCategory();
                } else {
                    System.out.println("Invalid crime category index.");
                }
            }
        } else {
            System.out.println("first add any crime category ");
        }
        return "";
    }


    public static String updatePassword(Scanner scanner) {
        while (true) {
            System.out.print("Enter new password (leave blank to keep, min 6 chars): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line;
            } else if (line.length() >= 6) {
                return line;
            } else {
                System.out.println("Password must be at least 6 characters.");
            }
        }
    }

    public static String updateCrimeCategoryIndexName(Scanner scanner) {
        CrimeCategoryDAO.viewAllCrimeCategory();
        LinkedList<CrimeCategory> list = CrimeCategoryDAO.getAllCategory();
        if (list != null) {
            while (true) {
                System.out.print("Enter Crime Category Index (leave blank to keep) : ");
                String id = scanner.nextLine();


                if (id.isEmpty()) {
                    return id;
                } else {
                    int idx = Integer.parseInt(id) - 1;
                    if (idx >= 0 && idx < list.size()) {
                        CrimeCategory cc = list.get(idx);
                        return cc.getCategory();
                    } else if (idx == 0) {
                        return "";
                    } else {
                        System.out.println("Invalid crime category index.");
                    }
                }

            }
        } else {
            System.out.println("first add any crime category ");
        }
        return "";
    }





    public static String updateUsername(Scanner scanner) {
        while (true) {
            System.out.print("Enter new username (leave blank to keep, 3-20 chars, letters/numbers/underscore): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line;
            } else if (line.matches("^[a-zA-Z0-9_]{3,20}$")) {
                return line;
            } else {
                System.out.println("Invalid username.");
            }
        }
    }







    // ---------- Password ----------
    public static String validatePassword(String password) {
        if (password == null) return "Password cannot be null.";
        int len = password.length();
        if (len < 8 || len > 10) {
            return "Password must be between 8 and 10 characters long.";
        }

        boolean hasUpper = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
            // lowercase letters are ignored – allowed but not required
        }
        if (!hasUpper) return "Password must contain at least one uppercase letter (A–Z).";
        if (!hasDigit) return "Password must contain at least one digit (0–9).";
        if (!hasSpecial) return "Password must contain at least one special character (e.g. !@#$%^&*).";
        return null; // valid
    }

    public static String password(Scanner scanner) {
        while (true) {
            System.out.print("Enter password (8–10 chars, at least 1 uppercase, 1 digit, 1 special): ");
            String password = scanner.nextLine().trim();
            String error = validatePassword(password);
            if (error == null) {
                return password; // valid
            } else {
                System.out.println("Invalid password: " + error);
            }
        }
    }


    public static String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty.";
        }

        String allowedSpecial = "._-@";
        int letterCount = 0;

        for (char c : username.toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount++;
            } else if (!Character.isDigit(c) && allowedSpecial.indexOf(c) == -1) {
                return "Username can only contain letters, digits, and these special characters: . _ - @";
            }
        }

        if (letterCount < 3) {
            return "Username must contain at least 3 alphabetic characters (A–Z, a–z).";
        }

        return null; // valid
    }

    public static String username(Scanner scanner) {
        while (true) {
            System.out.print("Enter username (letters, digits, . _ - @ allowed; at least 3 letters): ");
            String input = scanner.nextLine().trim();
            String error = validateUsername(input);
            if (error == null) {
                return input;
            } else {
                System.out.println("Invalid username: " + error);
            }
        }
    }










    // ---------- Role ----------
    public static String role(Scanner scanner) {
        while (true) {
            System.out.print("Enter role (ADMIN/OFFICER/STAFF): ");
            String line = scanner.nextLine().trim().toUpperCase();
            if (line.matches("ADMIN|OFFICER|STAFF")) {
                return line;
            } else {
                System.out.println("Invalid role. Must be ADMIN, OFFICER, or STAFF.");
            }
        }
    }

    public static String userNameByStationId(Scanner scanner) {
        LinkedList<User> list = UserDAO.getAllUserByStationId();
        while (true) {
            System.out.print("enter the index : ");
            int id = InputHelper.readInt(scanner) - 1;
            if (id >= 0 && id < list.size()) {
                return (list.get(id)).getUsername();
            } else {
                System.out.println("enter correct index");
            }
        }
    }

    public static String adminRoleByIndex(Scanner scanner) {
        while (true) {
            System.out.println("Select admin role:");
            System.out.println("1. Super Admin");
            System.out.println("2. Station Admin");
            System.out.print("Enter index : ");
            int choice = InputHelper.readInt(scanner);
            if (choice == 1) {
                return "Super Admin";
            } else if (choice == 2) {
                return "Station Admin";
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    public static String updateRole(Scanner scanner) {
        while (true) {
            System.out.print("Enter new role (leave blank to keep, ADMIN/OFFICER/STAFF): ");
            String line = scanner.nextLine().trim().toUpperCase();
            if (line.isEmpty()) {
                return line;
            } else if (line.matches("ADMIN|OFFICER|STAFF")) {
                return line;
            } else {
                System.out.println("Invalid role.");
            }
        }
    }

    // ---------- Full Name ----------
    public static String fullName(Scanner scanner) {
        while (true) {
            System.out.print("Enter full name (letters and spaces only): ");
            String line = scanner.nextLine().trim();
            if (!line.isEmpty() && line.matches("^[a-zA-Z ]+$")) {
                return line;
            } else {
                System.out.println("Invalid full name. Use only letters and spaces.");
            }
        }
    }


    public static String updateFullName(Scanner scanner) {
        while (true) {
            System.out.print("Enter new full name (leave blank to keep, letters/spaces only): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return line;
            } else if (line.matches("^[a-zA-Z ]+$")) {
                return line;
            } else {
                System.out.println("Invalid full name.");
            }
        }
    }


    public static int updateStationId(Scanner scanner) {
        while (true) {
            System.out.print("Enter the station ID (leave blank to keep): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return 0; // indicates "keep current value"
            }
            try {
                int id = Integer.parseInt(input);

                if (PoliceStationDAO.isStationIdValid(id)) {
                    return id;
                }
                System.out.println("Invalid station ID.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    public static String category(Scanner scanner) {
        while (true) {
            System.out.print("enter the category : ");
            String category = scanner.nextLine().trim();
            if (!category.isEmpty() && InputValidator.isValidName(category)) {
                return category;
            } else {
                System.out.println("Invalid category. Please enter a non-empty value.");
            }
        }
    }

}