package com.crms.model;

public class Staff {
    private int id;
    private int userId;
    private String employeeId;

    public Staff() {}

    public Staff(int id, int userId, String employeeId) {
        this.id = id;
        this.userId = userId;
        this.employeeId = employeeId;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", userId=" + userId +
                ", employeeId='" + employeeId + '\'' +
                '}';
    }
}