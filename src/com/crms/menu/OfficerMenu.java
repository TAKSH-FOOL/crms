package com.crms.menu;

import com.crms.Session;
import com.crms.model.*;
import com.crms.dao.*;
import com.crms.util.InputHelper;
import com.crms.util.InputValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class OfficerMenu {
    private static Scanner scanner = new Scanner(System.in);

    public static void show() {
        if (Session.getCurrentUser() == null || !"OFFICER".equals(Session.getCurrentUser().getRole())) {
            System.out.println("Access denied.");
            return;
        }
        Officer officer = OfficerDAO.getByUserId(Session.getCurrentUser().getId());
        if (officer == null) {
            System.out.println("Officer record not found. Please contact admin.");
            return;
        }
        int officerId = officer.getId();

        int choice;
        do {
            System.out.println("\n===== OFFICER MENU =====");
            System.out.println("1. View Assigned FIRs");
            System.out.println("2. Update FIR Status");
            System.out.println("3. Add Investigation Notes");
            System.out.println("4. Add Criminal");
            System.out.println("5. Update Criminal");
            System.out.println("6. Link Criminal to Crime");
            System.out.println("7. Add Evidence");
            System.out.println("8. Update Evidence");
            System.out.println("9. Add Victim");
            System.out.println("10. Update Victim");
            System.out.println("11. Add Witness");
            System.out.println("12. Update Witness");
            System.out.println("13. View Crime Records");
            System.out.println("14. Add Crime Record");
            System.out.println("15. Open Investigation");
            System.out.println("0. Logout");
            choice = InputHelper.readInt(scanner, "Enter choice: ");

            switch (choice) {
                case 1: viewAssignedFIRs(officerId); break;
                case 2: updateFIRStatus(officerId); break;
                case 3: addInvestigationNotes(officerId); break;
                case 4: addCriminal(); break;
                case 5: updateCriminal(); break;
                case 6: linkCriminalToCrime(); break;
                case 7: addEvidence(); break;
                case 8: updateEvidence(); break;
                case 9: addVictim(); break;
                case 10: updateVictim(); break;
                case 11: addWitness(); break;
                case 12: updateWitness(); break;
                case 13: viewCrimeRecords(); break;
                case 14: addCrimeRecord(); break;
                case 15: openInvestigation(officerId); break;
                case 0: System.out.println("Logging out..."); return;
                default: System.out.println("Invalid choice.");
            }
        } while (true);
    }

    // ---------- Existing methods (updated) ----------

    private static void viewAssignedFIRs(int officerId) {
        List<FIR> list = FIRDAO.getByOfficerId(officerId);
        if (list.isEmpty()) {
            System.out.println("No FIRs assigned to you.");
        } else {
            for (FIR f : list) System.out.println(f);
        }
    }

    private static void updateFIRStatus(int officerId) {
        System.out.print("Enter FIR number: ");
        String firNum = scanner.nextLine();
        FIR fir = FIRDAO.getByFIRNumber(firNum);
        if (fir == null || !fir.isActive()) {
            System.out.println("FIR not found.");
            return;
        }
        if (fir.getAssignedOfficerId() == null || fir.getAssignedOfficerId() != officerId) {
            System.out.println("This FIR is not assigned to you.");
            return;
        }
        System.out.println("Current status: " + fir.getStatus());
        System.out.print("New status (FILED/ASSIGNED/INVESTIGATING/CLOSED): ");
        String status = scanner.nextLine().toUpperCase();
        if (!status.matches("FILED|ASSIGNED|INVESTIGATING|CLOSED")) {
            System.out.println("Invalid status.");
            return;
        }
        fir.setStatus(status);
        if (FIRDAO.update(fir)) {
            System.out.println("FIR status updated.");
        } else {
            System.out.println("Failed to update FIR status.");
        }
    }

    private static void addInvestigationNotes(int officerId) {
        System.out.print("Enter FIR number: ");
        String firNum = scanner.nextLine();
        Investigation inv = InvestigationDAO.getByFIRNumber(firNum);
        if (inv == null || !inv.isActive()) {
            System.out.println("Investigation not found for this FIR. Please open one first.");
            return;
        }
        if (inv.getOfficerId() != officerId) {
            System.out.println("You are not assigned to this investigation.");
            return;
        }
        System.out.print("Enter additional notes: ");
        String notes = scanner.nextLine();
        String oldNotes = inv.getNotes();
        inv.setNotes((oldNotes != null ? oldNotes + "\n" : "") + notes);
        if (InvestigationDAO.update(inv)) {
            System.out.println("Notes added.");
        } else {
            System.out.println("Failed to add notes.");
        }
    }

    private static void addCriminal() {
        Criminal criminal = new Criminal();
        System.out.print("First Name: ");
        criminal.setFirstName(scanner.nextLine());
        System.out.print("Last Name: ");
        criminal.setLastName(scanner.nextLine());
        System.out.print("Date of Birth (YYYY-MM-DD): ");
        String dobStr = scanner.nextLine();
        if (!InputValidator.isValidDate(dobStr)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        criminal.setDob(LocalDate.parse(dobStr));
        System.out.print("Gender: ");
        criminal.setGender(scanner.nextLine());
        System.out.print("Address: ");
        criminal.setAddress(scanner.nextLine());
        System.out.print("Phone: ");
        criminal.setPhone(scanner.nextLine());
        System.out.print("Wanted Status (WANTED/NOT_WANTED): ");
        String ws = scanner.nextLine().toUpperCase();
        if (!ws.matches("WANTED|NOT_WANTED")) ws = "NOT_WANTED";
        criminal.setWantedStatus(ws);
        criminal.setActive(true);
        if (CriminalDAO.create(criminal)) {
            System.out.println("Criminal added with ID: " + criminal.getId());
        } else {
            System.out.println("Failed to add criminal.");
        }
    }

    private static void updateCriminal() {
        System.out.print("Enter Criminal ID: ");
        int id = InputHelper.readInt(scanner, "Criminal ID: ");
        Criminal criminal = CriminalDAO.getById(id);
        if (criminal == null) {
            System.out.println("Criminal not found.");
            return;
        }
        System.out.print("New First Name (leave blank to keep): ");
        String fn = scanner.nextLine();
        if (!fn.isEmpty()) criminal.setFirstName(fn);
        System.out.print("New Last Name (leave blank to keep): ");
        String ln = scanner.nextLine();
        if (!ln.isEmpty()) criminal.setLastName(ln);
        System.out.print("New Date of Birth (YYYY-MM-DD, leave blank): ");
        String dob = scanner.nextLine();
        if (!dob.isEmpty() && InputValidator.isValidDate(dob)) criminal.setDob(LocalDate.parse(dob));
        System.out.print("New Gender (leave blank): ");
        String g = scanner.nextLine();
        if (!g.isEmpty()) criminal.setGender(g);
        System.out.print("New Address (leave blank): ");
        String addr = scanner.nextLine();
        if (!addr.isEmpty()) criminal.setAddress(addr);
        System.out.print("New Phone (leave blank): ");
        String ph = scanner.nextLine();
        if (!ph.isEmpty()) criminal.setPhone(ph);
        System.out.print("New Wanted Status (WANTED/NOT_WANTED, leave blank): ");
        String ws = scanner.nextLine().toUpperCase();
        if (!ws.isEmpty()) {
            if (ws.matches("WANTED|NOT_WANTED")) criminal.setWantedStatus(ws);
            else System.out.println("Invalid status, keeping old.");
        }
        if (CriminalDAO.update(criminal)) {
            System.out.println("Criminal updated.");
        } else {
            System.out.println("Failed to update criminal.");
        }
    }

    private static void linkCriminalToCrime() {
        System.out.print("Enter Crime Number: ");
        String crimeNum = scanner.nextLine();
        CrimeRecord crime = CrimeDAO.getByCrimeNumber(crimeNum);
        if (crime == null) {
            System.out.println("Crime not found.");
            return;
        }
        System.out.print("Enter Criminal ID: ");
        int crimId = InputHelper.readInt(scanner, "Criminal ID: ");
        Criminal criminal = CriminalDAO.getById(crimId);
        if (criminal == null) {
            System.out.println("Criminal not found.");
            return;
        }
        System.out.print("Role (PERPETRATOR/ACCOMPLICE): ");
        String role = scanner.nextLine().toUpperCase();
        if (!role.matches("PERPETRATOR|ACCOMPLICE")) {
            System.out.println("Invalid role.");
            return;
        }
        if (CriminalDAO.linkCriminalToCrime(crimeNum, crimId, role)) {
            System.out.println("Criminal linked to crime.");
        } else {
            System.out.println("Failed to link (possibly duplicate).");
        }
    }

    private static void addEvidence() {
        Evidence evidence = new Evidence();
        System.out.print("Evidence Number: ");
        evidence.setEvidenceNumber(scanner.nextLine());
        System.out.print("FIR Number: ");
        evidence.setFirNumber(scanner.nextLine());
        System.out.print("Crime Number: ");
        evidence.setCrimeNumber(scanner.nextLine());
        System.out.print("Type (DOCUMENT/IMAGE/PHYSICAL/etc): ");
        evidence.setType(scanner.nextLine());
        System.out.print("Description: ");
        evidence.setDescription(scanner.nextLine());
        System.out.print("Custodian: ");
        evidence.setCustodian(scanner.nextLine());
        evidence.setActive(true);
        if (EvidenceDAO.create(evidence)) {
            System.out.println("Evidence added.");
        } else {
            System.out.println("Failed to add evidence. Evidence number may be duplicate.");
        }
    }

    private static void updateEvidence() {
        System.out.print("Enter Evidence Number: ");
        String evNum = scanner.nextLine();
        Evidence evidence = EvidenceDAO.getByEvidenceNumber(evNum);
        if (evidence == null) {
            System.out.println("Evidence not found.");
            return;
        }
        System.out.print("New FIR Number (leave blank): ");
        String fir = scanner.nextLine();
        if (!fir.isEmpty()) evidence.setFirNumber(fir);
        System.out.print("New Crime Number (leave blank): ");
        String crime = scanner.nextLine();
        if (!crime.isEmpty()) evidence.setCrimeNumber(crime);
        System.out.print("New Type (leave blank): ");
        String type = scanner.nextLine();
        if (!type.isEmpty()) evidence.setType(type);
        System.out.print("New Description (leave blank): ");
        String desc = scanner.nextLine();
        if (!desc.isEmpty()) evidence.setDescription(desc);
        System.out.print("New Custodian (leave blank): ");
        String cust = scanner.nextLine();
        if (!cust.isEmpty()) evidence.setCustodian(cust);
        if (EvidenceDAO.update(evidence)) {
            System.out.println("Evidence updated.");
        } else {
            System.out.println("Failed to update evidence.");
        }
    }

    private static void addVictim() {
        Victim victim = new Victim();
        System.out.print("FIR Number: ");
        victim.setFirNumber(scanner.nextLine());
        System.out.print("First Name: ");
        victim.setFirstName(scanner.nextLine());
        System.out.print("Last Name: ");
        victim.setLastName(scanner.nextLine());
        System.out.print("Contact: ");
        victim.setContact(scanner.nextLine());
        System.out.print("Address: ");
        victim.setAddress(scanner.nextLine());
        if (VictimDAO.create(victim)) {
            System.out.println("Victim added.");
        } else {
            System.out.println("Failed to add victim.");
        }
    }

    private static void updateVictim() {
        System.out.print("Enter Victim ID: ");
        int id = InputHelper.readInt(scanner, "Victim ID: ");
        Victim victim = VictimDAO.getById(id);
        if (victim == null) {
            System.out.println("Victim not found.");
            return;
        }
        System.out.print("New FIR Number (leave blank): ");
        String fir = scanner.nextLine();
        if (!fir.isEmpty()) victim.setFirNumber(fir);
        System.out.print("New First Name (leave blank): ");
        String fn = scanner.nextLine();
        if (!fn.isEmpty()) victim.setFirstName(fn);
        System.out.print("New Last Name (leave blank): ");
        String ln = scanner.nextLine();
        if (!ln.isEmpty()) victim.setLastName(ln);
        System.out.print("New Contact (leave blank): ");
        String contact = scanner.nextLine();
        if (!contact.isEmpty()) victim.setContact(contact);
        System.out.print("New Address (leave blank): ");
        String addr = scanner.nextLine();
        if (!addr.isEmpty()) victim.setAddress(addr);
        if (VictimDAO.update(victim)) {
            System.out.println("Victim updated.");
        } else {
            System.out.println("Failed to update victim.");
        }
    }

    private static void addWitness() {
        Witness witness = new Witness();
        System.out.print("FIR Number: ");
        witness.setFirNumber(scanner.nextLine());
        System.out.print("First Name: ");
        witness.setFirstName(scanner.nextLine());
        System.out.print("Last Name: ");
        witness.setLastName(scanner.nextLine());
        System.out.print("Contact: ");
        witness.setContact(scanner.nextLine());
        System.out.print("Statement: ");
        witness.setStatement(scanner.nextLine());
        if (WitnessDAO.create(witness)) {
            System.out.println("Witness added.");
        } else {
            System.out.println("Failed to add witness.");
        }
    }

    private static void updateWitness() {
        System.out.print("Enter Witness ID: ");
        int id = InputHelper.readInt(scanner, "Witness ID: ");
        Witness witness = WitnessDAO.getById(id);
        if (witness == null) {
            System.out.println("Witness not found.");
            return;
        }
        System.out.print("New FIR Number (leave blank): ");
        String fir = scanner.nextLine();
        if (!fir.isEmpty()) witness.setFirNumber(fir);
        System.out.print("New First Name (leave blank): ");
        String fn = scanner.nextLine();
        if (!fn.isEmpty()) witness.setFirstName(fn);
        System.out.print("New Last Name (leave blank): ");
        String ln = scanner.nextLine();
        if (!ln.isEmpty()) witness.setLastName(ln);
        System.out.print("New Contact (leave blank): ");
        String contact = scanner.nextLine();
        if (!contact.isEmpty()) witness.setContact(contact);
        System.out.print("New Statement (leave blank): ");
        String stmt = scanner.nextLine();
        if (!stmt.isEmpty()) witness.setStatement(stmt);
        if (WitnessDAO.update(witness)) {
            System.out.println("Witness updated.");
        } else {
            System.out.println("Failed to update witness.");
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

    // ---------- NEW methods ----------

    private static void addCrimeRecord() {
        CrimeRecord crime = new CrimeRecord();
        System.out.print("Crime Number: ");
        crime.setCrimeNumber(scanner.nextLine());
        System.out.print("FIR Number: ");
        crime.setFirNumber(scanner.nextLine());
        System.out.print("Crime Name: ");
        crime.setCrimeName(scanner.nextLine());
        System.out.print("Crime Description: ");
        crime.setCrimeDescription(scanner.nextLine());
        System.out.print("Crime Location: ");
        crime.setCrimeLocation(scanner.nextLine());
        System.out.print("Incident Date (YYYY-MM-DD HH:MM:SS): ");
        String dateStr = scanner.nextLine();
        try {
            crime.setIncidentDate(LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } catch (Exception e) {
            System.out.println("Invalid date, using current time.");
            crime.setIncidentDate(LocalDateTime.now());
        }
        System.out.print("Status (ACTIVE/SOLVED/CLOSED): ");
        String status = scanner.nextLine().toUpperCase();
        if (!status.matches("ACTIVE|SOLVED|CLOSED")) status = "ACTIVE";
        crime.setStatus(status);
        crime.setActive(true);
        if (CrimeDAO.create(crime)) {
            System.out.println("Crime record created.");
        } else {
            System.out.println("Failed to create crime record. Crime number may be duplicate.");
        }
    }

    private static void openInvestigation(int officerId) {
        System.out.print("Enter FIR number: ");
        String firNum = scanner.nextLine();
        FIR fir = FIRDAO.getByFIRNumber(firNum);
        if (fir == null || !fir.isActive()) {
            System.out.println("FIR not found.");
            return;
        }
        // Check if investigation already exists
        Investigation existing = InvestigationDAO.getByFIRNumber(firNum);
        if (existing != null && existing.isActive()) {
            System.out.println("Investigation already open for this FIR.");
            return;
        }
        Investigation inv = new Investigation();
        inv.setFirNumber(firNum);
        inv.setOfficerId(officerId);
        System.out.print("Initial notes: ");
        inv.setNotes(scanner.nextLine());
        inv.setStatus("OPEN");
        inv.setStartDate(LocalDateTime.now());
        inv.setActive(true);
        if (InvestigationDAO.create(inv)) {
            System.out.println("Investigation opened.");
        } else {
            System.out.println("Failed to open investigation.");
        }
    }
}