package com.crms.model;

import java.time.LocalDateTime;

public class Investigation {
    private int id;
    private String firNumber;
    private int officerId;
    private String notes;
    private String status;      // OPEN, CLOSED
    private LocalDateTime startDate;
    private boolean isActive;

    public Investigation() {}

    public Investigation(int id, String firNumber, int officerId, String notes,
                         String status, LocalDateTime startDate, boolean isActive) {
        this.id = id;
        this.firNumber = firNumber;
        this.officerId = officerId;
        this.notes = notes;
        this.status = status;
        this.startDate = startDate;
        this.isActive = isActive;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirNumber() { return firNumber; }
    public void setFirNumber(String firNumber) { this.firNumber = firNumber; }
    public int getOfficerId() { return officerId; }
    public void setOfficerId(int officerId) { this.officerId = officerId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "Investigation{" +
                "id=" + id +
                ", firNumber='" + firNumber + '\'' +
                ", officerId=" + officerId +
                ", status='" + status + '\'' +
                '}';
    }
}