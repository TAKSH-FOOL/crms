package com.crms.model;

import java.time.LocalDateTime;

public class CrimeRecord {
    private int id;
    private String crimeNumber;
    private String firNumber;
    private String crimeName;
    private String crimeDescription;
    private String crimeLocation;
    private LocalDateTime incidentDate;
    private String status;      // ACTIVE, SOLVED, CLOSED
    private boolean isActive;

    public CrimeRecord() {}

    public CrimeRecord(int id, String crimeNumber, String firNumber, String crimeName,
                       String crimeDescription, String crimeLocation, LocalDateTime incidentDate,
                       String status, boolean isActive) {
        this.id = id;
        this.crimeNumber = crimeNumber;
        this.firNumber = firNumber;
        this.crimeName = crimeName;
        this.crimeDescription = crimeDescription;
        this.crimeLocation = crimeLocation;
        this.incidentDate = incidentDate;
        this.status = status;
        this.isActive = isActive;
    }

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
        return "CrimeRecord{" +
                "id=" + id +
                ", crimeNumber='" + crimeNumber + '\'' +
                ", firNumber='" + firNumber + '\'' +
                ", crimeName='" + crimeName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}