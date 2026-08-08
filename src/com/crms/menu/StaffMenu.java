package com.crms.menu;

import com.crms.Session;
import com.crms.model.*;
import com.crms.dao.*;
import com.crms.util.InputHelper;
import com.crms.util.ReportGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
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
            System.out.println("8. Add Crime Category");
            System.out.println("9. Download FIR ");
            System.out.println("0. Logout");
            System.out.print( "Enter choice: ");
            choice = InputHelper.readInt(scanner);

            switch (choice) {
                case 1: registerFIR(); break;
                case 2: updateFIRDetails(); break;
                case 3: viewFIRStatus(); break;
                case 4: searchFIRs(); break;
                case 5: addVictim(); break;
                case 6: addWitness(); break;
                case 7: viewCrimeRecords(); break;
                case 8: addCategory(); break;
                case 9: downloadFIRPdf(); break;
                case 0: System.out.println("Logging out..."); return;
                default: System.out.println("Invalid choice.");
            }
        } while (true);
    }

    private static void registerFIR() {
        FIR fir = new FIR();
        fir.setComplainantName(InputHelper.complainantName(scanner));
        fir.setComplainantContact(InputHelper.complainantContact(scanner));
        fir.setIncidentLocation(InputHelper.incidentLocation(scanner));
        fir.setIncidentDate(InputHelper.incidentDateTime(scanner));
        fir.setIncidentDescription(InputHelper.incidentDescription(scanner));
        fir.setCrimeCategory(InputHelper.crimeCategoryIndexName(scanner));
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
        FIRDAO.displayFIRNumbersByStationId(Session.getCurrentUser().getStationId());
        String firNum = InputHelper.FIRNumber(scanner);
        FIR fir = FIRDAO.getByFIRNumber(firNum);
        if (fir == null || !fir.isActive()) {
            System.out.println("FIR not found.");
            return;
        }
        String name = InputHelper.updateComplainantName(scanner);
        if (!name.isEmpty()) fir.setComplainantName(name);

        String contact = InputHelper.updateComplainantContact(scanner);
        if (!contact.isEmpty()) fir.setComplainantContact(contact);

        String loc = InputHelper.updateIncidentLocation(scanner);
        if (!loc.isEmpty()) fir.setIncidentLocation(loc);

        String desc = InputHelper.updateIncidentDescription(scanner);
        if (!desc.isEmpty()) fir.setIncidentDescription(desc);

        String cat = InputHelper.updateCrimeCategoryIndexName(scanner);
        if (!cat.isEmpty()) fir.setCrimeCategory(cat);
        if (FIRDAO.update(fir)) {
            System.out.println("FIR details updated.");
        } else {
            System.out.println("Failed to update FIR details.");
        }
    }

    private static void viewFIRStatus() {
        FIRDAO.displayFIRNumbersByStationId(Session.getCurrentUser().getStationId());
        String firNum = InputHelper.FIRNumber(scanner);
        FIR fir = FIRDAO.getByFIRNumber(firNum);
        if (fir == null) {
            System.out.println("FIR not found.");
            return;
        }

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
        String status = InputHelper.FIRStatus(scanner);
        System.out.print("Complainant name (partial): ");
        String complainant = scanner.nextLine();

        System.out.print("From date (dd-MM-yyyy, leave blank): ");
        String from = InputHelper.date(scanner);
        System.out.print("To date (dd-MM-yyyy, leave blank): ");
        String to = InputHelper.date(scanner);

        LinkedList<FIR> globalList = FIRDAO.search(status, complainant, from, to);
        LinkedList<FIR> stationList = new LinkedList<>();
        for (FIR f : globalList) {
            if (f.getStationId() == Session.getCurrentUser().getStationId()) {
                stationList.add(f);
            }
        }
        if (stationList.isEmpty()) {
            System.out.println("No FIRs found.");
        } else {
            for (FIR f : stationList) {
                System.out.println(f);
            }
        }
    }
    private static void addVictim() {
        Victim victim = new Victim();
        if (FIRDAO.getAllByStationId(Session.getCurrentUser().getStationId()) != null){
            FIRDAO.displayFIRNumbersByStationId(Session.getCurrentUser().getStationId());
            victim.setFirNumber(InputHelper.FIRNumber(scanner));
            victim.setFirstName(InputHelper.firstName(scanner));
            victim.setLastName(InputHelper.lastName(scanner));
            victim.setContact(InputHelper.phone(scanner));
            victim.setAddress(InputHelper.address(scanner));
            victim.setHarm_description(InputHelper.harmDescription(scanner));
            if (VictimDAO.create(victim)) {
                System.out.println("Victim added.");
            } else {
                System.out.println("Failed to add victim.");
            }
        }
    }

    private static void addWitness() {
        Witness witness = new Witness();
        if (FIRDAO.getAllByStationId(Session.getCurrentUser().getStationId()) != null){
            FIRDAO.displayFIRNumbersByStationId(Session.getCurrentUser().getStationId());
            witness.setFirNumber(InputHelper.FIRNumber(scanner));
            witness.setFirstName(InputHelper.firstName(scanner));
            witness.setLastName(InputHelper.lastName(scanner));
            witness.setContact(InputHelper.phone(scanner));

            System.out.print("Statement: ");
            witness.setStatement(scanner.nextLine());

            if (WitnessDAO.create(witness)) {
                System.out.println("Witness added.");
            } else {
                System.out.println("Failed to add witness.");
            }
        }
    }

    private static void viewCrimeRecords() {
        LinkedList<CrimeRecord> list = CrimeDAO.getCrimeRecordsByStation(Session.getCurrentUser().getStationId());
        if (list.isEmpty()) {
            System.out.println("No crime records.");
        } else {
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i));
            }
        }
    }

    public static void addCategory(){
        String category = InputHelper.category(scanner);
        if (CrimeCategoryDAO.addCategory(category)) {
            System.out.println("Category added.");
        } else {
            System.out.println("Failed to add category.");
        }
    }

    private static void downloadFIRPdf() {
        FIRDAO.displayFIRNumbersByStationId(Session.getCurrentUser().getStationId());
        String firNum = InputHelper.FIRNumber(scanner);
        if (firNum == null) {
            System.out.println("No FIR selected.");
            return;
        }
        FIR fir = null;
        if (FIRDAO.isFIRCurrentUserStation(firNum)) {
            fir = FIRDAO.getByFIRNumber(firNum);
        }
        else {
            System.out.println("fir not found");
            return;
        }
        if (fir == null ) {
            System.out.println("FIR not found or inactive.");
            return;
        }

        String report = ReportGenerator.generateFIRReport(fir);
        if (report == null) {
            System.out.println("Failed to generate report.");
            return;
        }

        // Create reports directory if it doesn't exist
        File reportDir = new File("reports");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        String fileName = "FIR_" + fir.getFirNumber().replace("/", "_") + ".txt";
        File file = new File(reportDir, fileName);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(report);
            writer.flush();
            System.out.println("✅ Report saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save report: " + e.getMessage());
        }
    }
}