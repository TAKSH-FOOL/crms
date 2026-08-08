package com.crms.model;

public class Witness {
     int id;
     String firNumber;
     String firstName;
     String lastName;
     String contact;
     String statement;
     boolean isActive;
    public Witness() {}

    // Getters and setters

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirNumber() { return firNumber; }
    public void setFirNumber(String firNumber) { this.firNumber = firNumber; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getStatement() { return statement; }
    public void setStatement(String statement) { this.statement = statement; }

    @Override
    public String toString() {
        return "Witness{" +
                "id=" + id +
                ", firNumber='" + firNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}