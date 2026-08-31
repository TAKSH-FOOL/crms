package com.crms.menu;

import com.crms.Session;
import com.crms.ds.InvestigationBST;
import com.crms.model.*;
import com.crms.dao.*;
import com.crms.util.InputHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Scanner;

public class OfficerMenu {
    private static Scanner scanner = new Scanner(System.in);

    static int user_id = Session.getCurrentUser().getId();
    static int officer_id = OfficerDAO.getByUserId(user_id).getId();
    public static void show() {
        if (Session.getCurrentUser() == null || !"OFFICER".equals(Session.getCurrentUser().getRole())) {
            System.out.println("Access denied.");
            return;
        }
        Officer officer = OfficerDAO.getByUserId(user_id);
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
            System.out.println("7. Display All Criminals");
            System.out.println("8. Search Criminal");
            System.out.println("9. Add Evidence");
            System.out.println("10. Update Evidence");
            System.out.println("11. Add Victim");
            System.out.println("12. Update Victim");
            System.out.println("13. Add Witness");
            System.out.println("14. Update Witness");
            System.out.println("15. Search Investigation");
            System.out.println("16. View Crime Records");
            System.out.println("17. Add Crime Record");
            System.out.println("18. Open Investigation");
            System.out.println("19. Add Category");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            choice = InputHelper.readInt(scanner);

            switch (choice) {
                case 1:
                    viewAssignedFIRs(officerId);
                    break;
                case 2:
                    updateFIRStatus(officerId);
                    break;
                case 3:
                    addInvestigationNotes(officerId);
                    break;
                case 4:
                    addCriminal();
                    break;
                case 5:
                    updateCriminal();
                    break;
                case 6:
                    linkCriminalToCrime();
                    break;
                case 7:
                    CriminalDAO.displayAllCriminal();
                    break;
                case 8:
                    searchCriminal();
                    break;
                case 9:
                    addEvidence();
                    break;
                case 10:
                    updateEvidence();
                    break;
                case 11:
                    addVictim();
                    break;
                case 12:
                    updateVictim();
                    break;
                case 13:
                    addWitness();
                    break;
                case 14:
                    updateWitness();
                    break;
                case 15:
                    searchInvestigationByFir();
                    break;
                case 16:
                    viewCrimeRecords();
                    break;
                case 17:
                    addCrimeRecord();
                    break;
                case 18:
                    openInvestigation(officerId);
                    break;
                case 19:
                    addCategory();
                    break;
                case 0:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (true);
    }

    // ---------- Existing methods (updated) ----------

    private static void viewAssignedFIRs(int officerId) {
        LinkedList<FIR> list = FIRDAO.getByOfficerId(officerId);
        if (list.isEmpty()) {
            System.out.println("No FIRs assigned to you.");
        } else {
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i));
            }
        }
    }

    private static void updateFIRStatus(int officerId) {
        FIRDAO.displayFIRNumbersByOfficerId(officerId);
        if (FIRDAO.getByOfficerId(officerId).isEmpty()) {
            return;
        }
        String firNum = InputHelper.FIRNumber(scanner);
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
        String newStatus = InputHelper.FIRStatus(scanner);

        // Business rule: If changing to FILED, unassign the officer
        if ("FILED".equals(newStatus)) {
            fir.setAssignedOfficerId(null);  // remove officer assignment
            System.out.println("Officer unassigned because status changed to FILED.");
        }

        fir.setStatus(newStatus);
        if (FIRDAO.update(fir)) {
            System.out.println("FIR status updated.");
            // Optionally, also update investigation if any
            Investigation inv = InvestigationDAO.getByFIRNumber(firNum);
            if (inv != null && inv.isActive()) {
                if ("FILED".equals(newStatus) || "CLOSED".equals(newStatus)) {
                    // If closed or filed, mark investigation as closed/inactive
                    inv.setStatus("CLOSED");
                    inv.setActive(false);
                    InvestigationDAO.update(inv);
                } else if ("ASSIGNED".equals(newStatus) || "INVESTIGATING".equals(newStatus)) {
                    // If assigned or investigating, ensure investigation is open
                    inv.setStatus("OPEN");
                    inv.setActive(true);
                    InvestigationDAO.update(inv);
                }
            }
        } else {
            System.out.println("Failed to update FIR status.");
        }
    }

    private static void addInvestigationNotes(int officerId) {
        // Get FIR number using the existing method
        String firNum = InputHelper.getFirNumbersByOfficerId(scanner);

        if (firNum == null){
            return;
        }
        // Fetch the FIR to verify assignment
        FIR fir = FIRDAO.getByFIRNumber(firNum);
        if (fir == null || fir.getAssignedOfficerId() == null || fir.getAssignedOfficerId() != officerId) {
            System.out.println("You are not assigned to this FIR.");
            return;
        }

        // Now get the investigation
        Investigation inv = InvestigationDAO.getByFIRNumber(firNum);
        if (inv == null || !inv.isActive()) {
            System.out.println("Investigation not found for this FIR. Please open one first.");
            return;
        }

        // Add notes (no need to check inv.getOfficerId() anymore)
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

        String firstName = InputHelper.firstName(scanner);
        criminal.setFirstName(firstName);

        String lastName = InputHelper.lastName(scanner);
        criminal.setLastName(lastName);

        String dobStr = InputHelper.dob(scanner);
        criminal.setDob(LocalDate.parse(dobStr));

        String gender = InputHelper.gender(scanner);
        criminal.setGender(gender);

        String address = InputHelper.address(scanner);
        criminal.setAddress(address);

        String phone = InputHelper.phone(scanner);
        criminal.setPhone(phone);

        String ws = InputHelper.wantedStatus(scanner);
        criminal.setWantedStatus(ws);

        criminal.setActive(true);
        if (CriminalDAO.create(criminal)) {
            System.out.println("Criminal added with ID: " + criminal.getId());
        } else {
            System.out.println("Failed to add criminal.");
        }
    }

    private static void updateCriminal() {
        CriminalDAO.displayCriminalTable(CriminalDAO.getAllCriminal());
        System.out.print("Enter Criminal ID: ");
        int id = InputHelper.readInt(scanner);
        Criminal criminal = CriminalDAO.getById(id);
        if (criminal == null) {
            System.out.println("Criminal not found.");
            return;
        }
        String fn = InputHelper.updateFirstName(scanner);
        String ln = InputHelper.updateLastName(scanner);
        String dob = InputHelper.updateDob(scanner);
        String g = InputHelper.updateGender(scanner);
        String addr = InputHelper.updateAddress(scanner);
        String ph = InputHelper.updatePhone(scanner);
        String ws = InputHelper.updateWantedStatus(scanner);

        if (!fn.isEmpty()) criminal.setFirstName(fn);
        if (!ln.isEmpty()) criminal.setLastName(ln);
        if (!dob.isEmpty()) criminal.setDob(LocalDate.parse(dob));
        if (!g.isEmpty()) criminal.setGender(g);
        if (!addr.isEmpty()) criminal.setAddress(addr);
        if (!ph.isEmpty()) criminal.setPhone(ph);
        if (!ws.isEmpty()) criminal.setWantedStatus(ws);
        if (CriminalDAO.update(criminal)) {
            System.out.println("Criminal updated.");
        } else {
            System.out.println("Failed to update criminal.");
        }
    }

    private static void linkCriminalToCrime() {
        if (CrimeDAO.getCrimeRecordsByOfficerId(officer_id) != null && !CrimeDAO.getCrimeRecordsByOfficerId(officer_id).isEmpty()) {
            CrimeDAO.displayCrimeRecordTable(CrimeDAO.getCrimeRecordsByOfficerId(officer_id));
            String crimeNum = "";
            while (true){
                 crimeNum = InputHelper.CrimeNumber(scanner);
                if (!CrimeDAO.isCrimeRecordAssignedToOfficer(crimeNum, officer_id)) {
                    System.out.println("Crime record not found at the current station.");
                }
                else {
                    break;
                }
            }

            CrimeRecord crime = CrimeDAO.getByCrimeNumber(crimeNum);
            if (crime == null) {
                System.out.println("Crime not found.");
                return;
            }
            CriminalDAO.displayCriminalTable(CriminalDAO.getAllCriminal());
            System.out.print("Enter Criminal ID: ");
            int crimId = InputHelper.readInt(scanner);
            Criminal criminal = CriminalDAO.getById(crimId);
            if (criminal == null) {
                System.out.println("Criminal not found.");
                return;
            }
            String role = InputHelper.criminalRole(scanner);

            if (CriminalDAO.linkCriminalToCrime(crimeNum, crimId, role)) {
                System.out.println("Criminal linked to crime.");
            } else {
                System.out.println("Failed to link (possibly duplicate).");
            }
        }
        else {
            System.out.println("no crime record available.");
        }
    }

    private static void addEvidence() {
        Evidence evidence = new Evidence();
        if (FIRDAO.getByOfficerId(officer_id).isEmpty()) {
            System.out.println("No FIR numbers available.");
            return;
        }
        FIRDAO.displayFIRNumbersByOfficerId(officer_id);
        String firNum = "";
        while (true){
            firNum = InputHelper.FIRNumber(scanner);
            if (!FIRDAO.isFIRAssignedToOfficer(firNum, officer_id)) {
                System.out.println("FIR not found at the current station.");
                continue;
            }
            break;
        }

        evidence.setFirNumber(firNum);
        String crimeNum = null;  // default is NULL
        LinkedList<CrimeRecord> crimeRecords = CrimeDAO.getCrimeRecordsByOfficerId(officer_id);

        if (!crimeRecords.isEmpty()) {
            CrimeDAO.displayCrimeRecordTable(crimeRecords);
            while (true) {
                crimeNum = InputHelper.crimeNumberOrNull(scanner);
                if (crimeNum == null) {
                    // User wants to leave it blank
                    break;
                }
                if (CrimeDAO.isCrimeRecordAssignedToOfficer(crimeNum, officer_id)) {
                    // Valid and accessible
                    break;
                } else {
                    System.out.println("Invalid crime number or not assigned to you.");
                }
            }
        }
        evidence.setCrimeNumber(crimeNum);


        String evidenceType = InputHelper.evidenceType(scanner);
        evidence.setType(evidenceType);

        System.out.print("Description: ");
        evidence.setDescription(scanner.nextLine());

        System.out.print("Custodian: ");
        evidence.setCustodian(scanner.nextLine());

        System.out.print("Image file path (leave blank if none): ");
        String imgPath = scanner.nextLine().trim();
        if (!imgPath.isEmpty()) {
            // Optional: check if file exists
            File f = new File(imgPath);
            if (f.exists() && f.isFile()) {
                evidence.setImagePath(imgPath);
            } else {
                System.out.println("File not found. Image will not be stored.");
            }
        }

        evidence.setActive(true);
        if (EvidenceDAO.create(evidence)) {
            System.out.println("Evidence added.");
        } else {
            System.out.println("Failed to add evidence. Evidence number may be duplicate.");
        }
    }

    private static void updateEvidence() {
        // Get evidence records for this officer
        LinkedList<Evidence> evidenceList = EvidenceDAO.getEvidenceByOfficerId(officer_id);
        if (evidenceList == null || evidenceList.isEmpty()) {
            System.out.println("No evidence available for you.");
            return;
        }

        // Display and select evidence
        EvidenceDAO.displayEvidenceByOfficerId(officer_id);
        int idx;
        while (true) {
            System.out.print("Enter the index of the evidence to update: ");
            idx = InputHelper.readInt(scanner) - 1;
            if (idx >= 0 && idx < evidenceList.size()) {
                break;
            } else {
                System.out.println("Invalid index.");
            }
        }

        Evidence evidence = evidenceList.get(idx);
        if (evidence == null) {
            System.out.println("Evidence not found.");
            return;
        }

        // ----- Update Crime Number -----
        LinkedList<CrimeRecord> crimeRecords = CrimeDAO.getCrimeRecordsByOfficerId(officer_id);
        if (!crimeRecords.isEmpty()) {
            CrimeDAO.displayCrimeRecordTable(crimeRecords);
            while (true) {
                System.out.print("Enter new Crime Number (or press Enter to set to NULL): ");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    evidence.setCrimeNumber(null);  // Allow NULL
                    break;
                } else if (CrimeDAO.isCrimeRecordAssignedToOfficer(input, officer_id)) {
                    evidence.setCrimeNumber(input);
                    break;
                } else {
                    System.out.println("Invalid crime number or not assigned to you.");
                }
            }
        } else {
            // No crime records available, set to NULL
            evidence.setCrimeNumber(null);
            System.out.println("No crime records available. Crime number set to NULL.");
        }

        // ----- Update other fields -----
        String type = InputHelper.evidenceType(scanner);
        if (!type.isEmpty()) evidence.setType(type);

        System.out.print("New Description (leave blank to keep): ");
        String desc = scanner.nextLine();
        if (!desc.isEmpty()) evidence.setDescription(desc);

        System.out.print("New Custodian (leave blank to keep): ");
        String cust = scanner.nextLine();
        if (!cust.isEmpty()) evidence.setCustodian(cust);

        System.out.print("New Image file path (leave blank to keep): ");
        String imgPath = scanner.nextLine().trim();
        if (!imgPath.isEmpty()) {
            File f = new File(imgPath);
            if (f.exists() && f.isFile()) {
                evidence.setImagePath(imgPath);
            } else {
                System.out.println("File not found. Image will not be updated.");
            }
        }

        // ----- Save changes -----
        if (EvidenceDAO.update(evidence)) {
            System.out.println("Evidence updated successfully.");
        } else {
            System.out.println("Failed to update evidence.");
        }
    }

    private static void addVictim() {
        Victim victim = new Victim();
        if (FIRDAO.getByOfficerId(officer_id).isEmpty()) {
            System.out.println("No FIR numbers available.");
            return;
        }
        FIRDAO.displayFIRNumbersByOfficerId(officer_id);
        String firNumber = "";
        while (true){
            firNumber = InputHelper.FIRNumber(scanner);
            if (FIRDAO.isFIRAssignedToOfficer(firNumber, officer_id)) {
                victim.setFirNumber(firNumber);
                break;
            }
            System.out.println("Invalid FIR number. Please try again.");
        }
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

    private static void updateVictim() {
        if (!VictimDAO.getVictimsByOfficerId(officer_id).isEmpty() ) {
            VictimDAO.displayVictimsByOfficerId(officer_id);
            int id = 0;
            while (true){
                System.out.print("Enter Victim ID: ");
                id = InputHelper.readInt(scanner);
                if (!VictimDAO.isVictimAssignedToOfficer(id, officer_id)) {
                    System.out.println("Victim is not associated with your station.");
                    continue;
                }
                break;
            }

            Victim victim = VictimDAO.getById(id);
            if (victim == null) {
                System.out.println("Victim not found.");
                return;
            }

            String fn = InputHelper.updateFirstName(scanner);
            if (!fn.isEmpty()) victim.setFirstName(fn);
            String ln = InputHelper.updateLastName(scanner);
            if (!ln.isEmpty()) victim.setLastName(ln);
            String contact = InputHelper.updatePhone(scanner);
            if (!contact.isEmpty()) victim.setContact(contact);
            String addr = InputHelper.updateAddress(scanner);
            if (!addr.isEmpty()) victim.setAddress(addr);

            String harm_Description = InputHelper.harmDescription(scanner);
            if (!harm_Description.isEmpty()) victim.setHarm_description(harm_Description);
            if (VictimDAO.update(victim)) {
                System.out.println("Victim updated.");
            } else {
                System.out.println("Failed to update victim.");
            }
        } else {
            System.out.println("there is no victim ");
        }
    }

    private static void addWitness() {
        Witness witness = new Witness();
        if (FIRDAO.getByOfficerId(officer_id).isEmpty()) {
            System.out.println("No FIR numbers available.");
            return;
        }
        String firNumber = "";
        FIRDAO.displayFIRNumbersByOfficerId(officer_id);
        while (true) {
            firNumber = InputHelper.FIRNumber(scanner);
            if (!FIRDAO.isFIRAssignedToOfficer(firNumber, officer_id)) {
                System.out.println("FIR is not assigned to you.");
                return;
            } else {
                break;
            }
        }

        witness.setFirNumber(firNumber);
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

    private static void updateWitness() {
        if (WitnessDAO.getWitnessesByOfficerId(officer_id).isEmpty()) {
            System.out.println("No active witnesses available.");
            return;
        }
        int id = 0;
        WitnessDAO.displayWitnessesByOfficerId(officer_id);
        while (true) {
            System.out.print("Enter Witness ID: ");
            id = InputHelper.readInt(scanner);
            if (!WitnessDAO.isWitnessAssignedToOfficer(id, officer_id)) {
                System.out.println("Witness is not assigned to you.");
                return;
            } else {
                break;
            }
        }

        Witness witness = WitnessDAO.getById(id);
        if (witness == null) {
            System.out.println("Witness not found.");
            return;
        }

        String fn = InputHelper.updateFirstName(scanner);
        if (!fn.isEmpty()) witness.setFirstName(fn);

        String ln = InputHelper.updateLastName(scanner);
        if (!ln.isEmpty()) witness.setLastName(ln);

        String contact = InputHelper.updatePhone(scanner);
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

        LinkedList<CrimeRecord> list = CrimeDAO.getCrimeRecordsByOfficerId(officer_id);
        if (list.isEmpty()) {
            System.out.println("No crime records.");
        } else {
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i));
            }
        }
    }

    // ---------- NEW methods ----------

    private static void addCrimeRecord() {
        CrimeRecord crime = new CrimeRecord();
        if (FIRDAO.getByOfficerId(officer_id) != null && !FIRDAO.getByOfficerId(officer_id).isEmpty() ) {
            FIRDAO.displayFIRNumbersByOfficerId(officer_id);
            while (true){
                System.out.print("Enter FIR Number: ");
                String firNumber = scanner.nextLine();
                if (FIRDAO.isFIRAssignedToOfficer(firNumber, officer_id)) {
                    crime.setFirNumber(firNumber);
                    break;
                } else {
                    System.out.println("FIR is not assigned to you.");
                }
            }
        } else {
            System.out.println("No active FIRs available.");
            return;
        }
        crime.setCrimeName(InputHelper.crimeName(scanner));
        crime.setCrimeDescription(InputHelper.crimeDescription(scanner));
        crime.setCrimeLocation(InputHelper.crimeLocation(scanner));
        crime.setIncidentDate(InputHelper.incidentDateTime(scanner));
        crime.setStatus(InputHelper.crimeStatus(scanner));
        crime.setActive(true);
        if (CrimeDAO.create(crime)) {
            System.out.println("Crime record created.");
        } else {
            System.out.println("Failed to create crime record. Crime number may be duplicate.");
        }
    }

    private static void openInvestigation(int officerId) {
        String firNum = InputHelper.getFirNumbersByOfficerId(scanner);
        if (firNum == null) {
            System.out.println("No FIR selected.");
            return;
        }

        FIR fir = FIRDAO.getByFIRNumber(firNum);
        if (fir == null || !fir.isActive()) {
            System.out.println("FIR not found or inactive.");
            return;
        }

        Investigation existing = InvestigationDAO.getByFIRNumber(firNum);

        if (existing != null) {
            // Check if it's already active
            if (existing.isActive()) {
                System.out.println("Investigation already open for this FIR.");
                return;
            } else {
                // Reactivate the inactive investigation
                existing.setOfficerId(officerId);
                // Add a note about reopening
                String reopenNote = "Investigation reopened by officer ID " + officerId + " on " + LocalDateTime.now();
                String notes = existing.getNotes();
                existing.setNotes((notes != null && !notes.isEmpty()) ? notes + "\n" + reopenNote : reopenNote);
                existing.setStatus("OPEN");
                existing.setActive(true);
                existing.setStartDate(LocalDateTime.now());

                if (InvestigationDAO.update(existing)) {
                    System.out.println("Investigation reopened.");
                } else {
                    System.out.println("Failed to reopen investigation.");
                }
                return;
            }
        }

        // No investigation exists – create a new one
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

    public static void addCategory() {
        String category = InputHelper.category(scanner);
        if (CrimeCategoryDAO.addCategory(category)) {
            System.out.println("Category added.");
        } else {
            System.out.println("Failed to add category.");
        }
    }

    private static void searchCriminal() {
        String firstName = InputHelper.updateFirstName(scanner);
        if (firstName.isEmpty()) firstName = null;

        String lastName = InputHelper.updateLastName(scanner);
        if (lastName.isEmpty()) lastName = null;

        String status= "";
        while (true){
            System.out.print("Enter wanted status (WANTED/NOT_WANTED, or press Enter to skip): ");
            status = scanner.nextLine().trim().toUpperCase();
            if (status.isEmpty()) {
                status = null;
            } else if (!status.matches("WANTED|NOT_WANTED")) {
                System.out.println("Invalid status.");
                continue;
            }
            break;
        }


        LinkedList<Criminal> results = CriminalDAO.searchCriminal(firstName, lastName, status);
        if (results.isEmpty()) {
            System.out.println("No criminals found.");
        } else {
            System.out.println("Search results:");
            for (Criminal c : results) {
                System.out.println(c);
            }
        }
    }

    // In your StaffMenu or wherever
    public static void searchInvestigationByFir() {
        String firNumber = InputHelper.getFirNumbersByOfficerId(scanner);// your existing helper
        InvestigationBST bst = new InvestigationBST();
        Investigation i = InvestigationDAO.getByFIRNumber(firNumber);
        bst.insert(i);
        Investigation inv = bst.searchByFirNumber(firNumber);
        if (inv != null) {
            System.out.println("Investigation found: " + inv);
            // display details
        } else {
            System.out.println("No investigation found for FIR: " + firNumber);
        }
    }
}