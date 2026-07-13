package com.crms.model;

import java.time.LocalDateTime;

public class FIR {
    private int id;
    private String firNumber;
    private String complainantName;
    private String complainantContact;
    private String incidentLocation;
    private LocalDateTime incidentDate;
    private String incidentDescription;
    private String crimeCategory;
    private String status;       // FILED, ASSIGNED, INVESTIGATING, CLOSED
    private Integer assignedOfficerId;
    private int stationId;
    private boolean isActive;
    private LocalDateTime createdAt;

    public FIR() {}

    public FIR(int id, String firNumber, String complainantName, String complainantContact,
               String incidentLocation, LocalDateTime incidentDate, String incidentDescription,
               String crimeCategory, String status, Integer assignedOfficerId,
               int stationId, boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.firNumber = firNumber;
        this.complainantName = complainantName;
        this.complainantContact = complainantContact;
        this.incidentLocation = incidentLocation;
        this.incidentDate = incidentDate;
        this.incidentDescription = incidentDescription;
        this.crimeCategory = crimeCategory;
        this.status = status;
        this.assignedOfficerId = assignedOfficerId;
        this.stationId = stationId;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

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
        return "FIR{" +
                "id=" + id +
                ", firNumber='" + firNumber + '\'' +
                ", complainantName='" + complainantName + '\'' +
                ", status='" + status + '\'' +
                ", crimeCategory='" + crimeCategory + '\'' +
                ", assignedOfficerId=" + assignedOfficerId +
                ", stationId=" + stationId +
                ", createdAt=" + createdAt +
                '}';
    }
}