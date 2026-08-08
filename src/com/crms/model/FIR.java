package com.crms.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FIR {
     int id;
     String firNumber;
     String complainantName;
     String complainantContact;
     String incidentLocation;
     LocalDateTime incidentDate;
     String incidentDescription;
     String crimeCategory;
     String status;       // FILED, ASSIGNED, INVESTIGATING, CLOSED
     Integer assignedOfficerId;
     int stationId;
     boolean isActive;
     LocalDateTime createdAt;


    public FIR() {}

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirNumber() { return firNumber; }
    public void setFirNumber(String firNumber) { this.firNumber = firNumber; }
    public String getComplainantName() { return complainantName; }
    public void setComplainantName(String complainantName) { this.complainantName = complainantName; }
    public String getComplainantContact() { return complainantContact; }
    public void setComplainantContact(String complainantContact) { this.complainantContact = complainantContact; }
    public String getIncidentLocation() { return incidentLocation; }
    public void setIncidentLocation(String incidentLocation) { this.incidentLocation = incidentLocation; }
    public LocalDateTime getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDateTime incidentDate) { this.incidentDate = incidentDate; }
    public String getIncidentDescription() { return incidentDescription; }
    public void setIncidentDescription(String incidentDescription) { this.incidentDescription = incidentDescription; }
    public String getCrimeCategory() { return crimeCategory; }
    public void setCrimeCategory(String crimeCategory) { this.crimeCategory = crimeCategory; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getAssignedOfficerId() { return assignedOfficerId; }
    public void setAssignedOfficerId(Integer assignedOfficerId) { this.assignedOfficerId = assignedOfficerId; }
    public int getStationId() { return stationId; }
    public void setStationId(int stationId) { this.stationId = stationId; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");        StringBuilder sb = new StringBuilder();

        sb.append("┌──────────────────────────────────────────────┐\n");
        sb.append("│              FIR DETAILS                     │\n");
        sb.append("├──────────────────────────────────────────────┤\n");

        appendField(sb, "FIR Number", firNumber != null ? firNumber : "N/A");
        appendField(sb, "ID", String.valueOf(id));
        appendField(sb, "Complainant", complainantName != null ? complainantName : "N/A");
        appendField(sb, "Contact", complainantContact != null ? complainantContact : "N/A");
        appendField(sb, "Location", incidentLocation != null ? incidentLocation : "N/A");

        // Multi-line description (wraps at 30 chars, indents continuation lines)
        appendMultiLineField(sb, "Description", incidentDescription != null ? incidentDescription : "N/A", 30);

        appendField(sb, "Status", status != null ? status : "N/A");
        appendField(sb, "Crime Category", crimeCategory != null ? crimeCategory : "N/A");
        appendField(sb, "Assigned Officer", assignedOfficerId != null ? "ID: " + assignedOfficerId : "None");
        appendField(sb, "Created At", createdAt != null ? createdAt.format(formatter) : "N/A");

        sb.append("└──────────────────────────────────────────────┘");
        return sb.toString();
    }

    /**
     * Appends a single‑line field (label: value) with fixed width.
     */
    private void appendField(StringBuilder sb, String label, String value) {
        sb.append(String.format("%-20s : %-30s \n", label, value));
    }

    /**
     * Appends a multi‑line field where the value is wrapped at maxLen.
     * Continuation lines are indented to align with the start of the value.
     */
    private void appendMultiLineField(StringBuilder sb, String label, String value, int maxLen) {
        // First line: label + colon + space + first part (or whole if short)
        sb.append(String.format("%-20s : ", label));
        if (value.length() <= maxLen) {
            sb.append(String.format("%-30s \n", value));
            return;
        }

        // First chunk
        String firstPart = value.substring(0, maxLen);
        sb.append(String.format("%-30s \n", firstPart));

        // Remaining chunks – indent with 22 spaces (20 for label + colon + space)
        String rest = value.substring(maxLen);
        while (!rest.isEmpty()) {
            int take = Math.min(maxLen, rest.length());
            String part = rest.substring(0, take);
            rest = rest.substring(take);
            sb.append(String.format("%22s%-30s \n", " ", part)); // 22 spaces indent
        }
    }
}