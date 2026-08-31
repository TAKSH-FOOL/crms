package com.crms.model;

import java.time.LocalDateTime;

public class Investigation {
     int id;
     String firNumber;
     int officerId;
     String notes;
     String status;      // OPEN, CLOSED
     LocalDateTime startDate;
     boolean isActive;

    public Investigation() {}



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
        return String.format(
                "Investigation #%d [FIR: %s] | Officer: %d | Status: %s | Started: %s | Active: %s | Notes: %s",
                id,
                firNumber,
                officerId,
                status != null ? status : "N/A",
                startDate != null ? startDate : "N/A",
                isActive ? "Yes" : "No",
                notes != null ? (notes.length() > 30 ? notes.substring(0, 30) + "…" : notes) : "No notes"
        );
    }
}