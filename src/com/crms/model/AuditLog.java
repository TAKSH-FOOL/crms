package com.crms.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class AuditLog {
     int id;
     int userId;
     String username;
     String action;
     String tableName;
     String recordId;
     String oldValue;
     String newValue;
     LocalDateTime timestamp;

    public AuditLog() {}

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }




    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("┌──────────────────────────────────────────────────────────┐\n");
        sb.append("│                    AUDIT LOG ENTRY                       │\n");
        sb.append("├──────────────────────────────────────────────────────────┤\n");
        sb.append(String.format("%-20s : %-40s \n", "ID", id));
        sb.append(String.format("%-20s : %-40s \n", "Username", username != null ? username : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Action", action != null ? action : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Table", tableName != null ? tableName : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Record ID", recordId != null ? recordId : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Timestamp",
                timestamp != null ? timestamp.format(formatter) : "N/A"));
        sb.append("├──────────────────────────────────────────────────────────┤\n");
        sb.append("│                      OLD VALUE                           │\n");
        sb.append("├──────────────────────────────────────────────────────────┤\n");
        sb.append(String.format("%-54s \n", oldValue != null ? oldValue : "N/A"));
        sb.append("├──────────────────────────────────────────────────────────┤\n");
        sb.append("│                      NEW VALUE                           │\n");
        sb.append("├──────────────────────────────────────────────────────────┤\n");
        sb.append(String.format("%-54s \n", newValue != null ? newValue : "N/A"));
        sb.append("└──────────────────────────────────────────────────────────┘");
        return sb.toString();
    }
}