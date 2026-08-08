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


    public String toString() {
        return "Investigation{" +
                "id=" + id +
                ", firNumber='" + firNumber + '\'' +
                ", officerId=" + officerId +
                ", status='" + status + '\'' +
                '}';
    }
}