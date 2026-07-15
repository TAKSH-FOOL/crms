package com.crms.menu;

import com.crms.Session;
import com.crms.model.*;
import com.crms.dao.*;
import com.crms.util.InputHelper;
import com.crms.util.InputValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class StaffMenu {
    private static Scanner scanner = new Scanner(System.in);

    public static void show() {
        if (Session.getCurrentUser() == null || !"STAFF".equals(Session.getCurrentUser().getRole())) {
            System.out.println("Access denied.");
            return;
        }
        int choice;
        do {
            System.out.println("\n===== STAFF MENU =====");
            System.out.println("1. Register FIR");
            System.out.println("2. Update FIR Details");
            System.out.println("3. View FIR Status");
            System.out.println("4. Search FIRs");
            System.out.println("5. Add Victim");
            System.out.println("6. Add Witness");
            System.out.println("7. View Crime Records");
            System.out.println("0. Logout");
            choice = InputHelper.readInt(scanner, "Enter choice: ");

            switch (choice) {
                case 1: registerFIR(); break;
                case 2: updateFIRDetails(); break;
                case 3: viewFIRStatus(); break;
                case 4: searchFIRs(); break;
                case 5: addVictim(); break;
                case 6: addWitness(); break;
                case 7: viewCrimeRecords(); break;
                case 0: System.out.println("Logging out..."); return;
                default: System.out.println("Invalid choice.");
            }
        } while (true);
    }

    private static void registerFIR() {
        FIR fir = new FIR();
        System.out.print("FIR Number: ");
        fir.setFirNumber(scanner.nextLine());
        if (FIRDAO.getByFIRNumber(fir.getFirNumber()) != null) {
            System.out.println("FIR number already exists.");
            return;
        }
        System.out.print("Complainant Name: ");
        fir.setComplainantName(scanner.nextLine());
        System.out.print("Complainant Contact: ");
        String contact = scanner.nextLine();
        if (!InputValidator.isValidPhone(contact)) {
            System.out.println("Invalid complainant contact.");
            return;
        }
        fir.setComplainantContact(contact);
        System.out.print("Incident Location: ");
        fir.setIncidentLocation(scanner.nextLine());
        System.out.print("Incident Date (YYYY-MM-DD HH:MM:SS): ");
        String dateStr = scanner.nextLine();
        try {
            LocalDateTime date = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            fir.setIncidentDate(date);
        } catch (Exception e) {
            System.out.println("Invalid date. Using current time.");
            fir.setIncidentDate(LocalDateTime.now());
        }
        System.out.print("Incident Description: ");
        fir.setIncidentDescription(scanner.nextLine());
        System.out.print("Crime Category: ");
        fir.setCrimeCategory(scanner.nextLine());
        fir.setStatus("FILED");
        fir.setAssignedOfficerId(null);
        fir.setStationId(Session.getCurrentUser().getStationId());
        fir.setActive(true);
        if (FIRDAO.create(fir)) {
            System.out.println("FIR registered with number: " + fir.getFirNumber());
        } else {
            System.out.println("Failed to register FIR.");
        }
    }

    private static void updateFIRDetails() {
        System.out.print("Enter FIR number: ");
        String firNum = scanner.nextLine();
        FIR fir = FIRDAO.getByFIRNumber(firNum);
        if (fir == null || !fir.isActive()) {
            System.out.println("FIR not found.");
            return;
        }
        if (fir.getStationId() != Session.getCurrentUser().getStationId()) {
            System.out.println("You can only update FIRs from your station.");
            return;
        }
        System.out.print("New Complainant Name (leave blank to keep): ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) fir.setComplainantName(name);
        System.out.print("New Complainant Contact (leave blank): ");
        String contact = scanner.nextLine();
        if (!contact.isEmpty()) {
            if (!InputValidator.isValidPhone(contact)) {
                System.out.println("Invalid complainant contact.");
                return;
            }
            fir.setComplainantContact(contact);
        }
        System.out.print("New Incident Location (leave blank): ");
        String loc = scanner.nextLine();
        if (!loc.isEmpty()) fir.setIncidentLocation(loc);
        System.out.print("New Incident Description (leave blank): ");
        String desc = scanner.nextLine();
        if (!desc.isEmpty()) fir.setIncidentDescription(desc);
        System.out.print("New Crime Category (leave blank): ");
        String cat = scanner.nextLine();
        if (!cat.isEmpty()) fir.setCrimeCategory(cat);
        if (FIRDAO.update(fir)) {
            System.out.println("FIR details updated.");
        } else {
            System.out.println("Failed to update FIR details.");
        }
    }

    private static void viewFIRStatus() {
        System.out.print("Enter FIR number: ");
        String firNum = scanner.nextLine();
        FIR fir = FIRDAO.getByFIRNumber(firNum);
        if (fir == null) {
            System.out.println("FIR not found.");
            return;
        }
        if (fir.getStationId() != Session.getCurrentUser().getStationId()) {
            System.out.println("You can only view FIRs from your station.");
            return;
        }
        System.out.println(fir);
        System.out.println("Status: " + fir.getStatus());
        if (fir.getAssignedOfficerId() != null) {
            Officer off = OfficerDAO.getById(fir.getAssignedOfficerId());
            if (off != null) {
                User user = UserDAO.getById(off.getUserId());
                System.out.println("Assigned Officer: " + (user != null ? user.getFullName() : "Unknown"));
            }
        }
    }

    private static void searchFIRs() {
        System.out.print("Filter by status (leave blank for all): ");
        String status = scanner.nextLine();
        if (!status.isEmpty() && !status.matches("FILED|ASSIGNED|INVESTIGATING|CLOSED")) {
            System.out.println("Invalid status.");
            return;
        }
        System.out.print("Complainant name (partial): ");
        String complainant = scanner.nextLine();
        System.out.print("From date (YYYY-MM-DD, leave blank): ");
        String from = scanner.nextLine();
        System.out.print("To date (YYYY-MM-DD, leave blank): ");
        String to = scanner.nextLine();
        if (!from.isEmpty() && !InputValidator.isValidDate(from)) {
            System.out.println("Invalid from date.");
            return;
        }
        if (!to.isEmpty() && !InputValidator.isValidDate(to)) {
            System.out.println("Invalid to date.");
            return;
        }
        List<FIR> list = FIRDAO.searchByStation(Session.getCurrentUser().getStationId(), status, complainant, from, to);
        if (list.isEmpty()) {
            System.out.println("No FIRs found.");
        } else {
            for (FIR f : list) System.out.println(f);
        }
    }

    private static void addVictim() {
        Victim victim = new Victim();
        System.out.print("FIR Number: ");
        String firNumber = scanner.nextLine();
        FIR fir = FIRDAO.getByFIRNumber(firNumber);
        if (fir == null || !fir.isActive() || fir.getStationId() != Session.getCurrentUser().getStationId()) {
            System.out.println("Invalid FIR for your station.");
            return;
        }
        victim.setFirNumber(firNumber);
        System.out.print("First Name: ");
        victim.setFirstName(scanner.nextLine());
        System.out.print("Last Name: ");
        victim.setLastName(scanner.nextLine());
        System.out.print("Contact: ");
        String contact = scanner.nextLine();
        if (!InputValidator.isValidPhone(contact)) {
            System.out.println("Invalid contact.");
            return;
        }
        victim.setContact(contact);
        System.out.print("Address: ");
        victim.setAddress(scanner.nextLine());
        if (VictimDAO.create(victim)) {
            System.out.println("Victim added.");
        } else {
            System.out.println("Failed to add victim.");
        }
    }

    private static void addWitness() {
        Witness witness = new Witness();
        System.out.print("FIR Number: ");
        String firNumber = scanner.nextLine();
        FIR fir = FIRDAO.getByFIRNumber(firNumber);
        if (fir == null || !fir.isActive() || fir.getStationId() != Session.getCurrentUser().getStationId()) {
            System.out.println("Invalid FIR for your station.");
            return;
        }
        witness.setFirNumber(firNumber);
        System.out.print("First Name: ");
        witness.setFirstName(scanner.nextLine());
        System.out.print("Last Name: ");
        witness.setLastName(scanner.nextLine());
        System.out.print("Contact: ");
        String contact = scanner.nextLine();
        if (!InputValidator.isValidPhone(contact)) {
            System.out.println("Invalid contact.");
            return;
        }
        witness.setContact(contact);
        System.out.print("Statement: ");
        witness.setStatement(scanner.nextLine());
        if (WitnessDAO.create(witness)) {
            System.out.println("Witness added.");
        } else {
            System.out.println("Failed to add witness.");
        }
    }

    private static void viewCrimeRecords() {
        List<CrimeRecord> list = CrimeDAO.getAllActive();
        if (list.isEmpty()) {
            System.out.println("No crime records.");
        } else {
            for (CrimeRecord c : list) System.out.println(c);
        }
    }
}