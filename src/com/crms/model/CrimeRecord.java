package com.crms.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CrimeRecord {
     int id;
     String crimeNumber;
     String firNumber;
     String crimeName;
     String crimeDescription;
     String crimeLocation;
     LocalDateTime incidentDate;
     String status;      // ACTIVE, SOLVED, CLOSED
     boolean isActive;

    public CrimeRecord() {}

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCrimeNumber() { return crimeNumber; }
    public void setCrimeNumber(String crimeNumber) { this.crimeNumber = crimeNumber; }
    public String getFirNumber() { return firNumber; }
    public void setFirNumber(String firNumber) { this.firNumber = firNumber; }
    public String getCrimeName() { return crimeName; }
    public void setCrimeName(String crimeName) { this.crimeName = crimeName; }
    public String getCrimeDescription() { return crimeDescription; }
    public void setCrimeDescription(String crimeDescription) { this.crimeDescription = crimeDescription; }
    public String getCrimeLocation() { return crimeLocation; }
    public void setCrimeLocation(String crimeLocation) { this.crimeLocation = crimeLocation; }
    public LocalDateTime getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDateTime incidentDate) { this.incidentDate = incidentDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }


    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("┌──────────────────────────────────────────────────────────┐\n");
        sb.append("│                  CRIME RECORD DETAILS                  │\n");
        sb.append("├──────────────────────────────────────────────────────────┤\n");
        sb.append(String.format("%-20s : %-40s \n", "ID", id));
        sb.append(String.format("%-20s : %-40s \n", "Crime Number", crimeNumber != null ? crimeNumber : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "FIR Number", firNumber != null ? firNumber : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Crime Name", crimeName != null ? crimeName : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Description",
                crimeDescription != null ?
                        (crimeDescription.length() > 50 ? crimeDescription.substring(0, 47) + "..." : crimeDescription)
                        : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Location", crimeLocation != null ? crimeLocation : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Incident Date",
                incidentDate != null ? incidentDate.format(formatter) : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Status", status != null ? status : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Active", isActive ? "✅ Yes" : "❌ No"));
        sb.append("└──────────────────────────────────────────────────────────┘");
        return sb.toString();
    }
}