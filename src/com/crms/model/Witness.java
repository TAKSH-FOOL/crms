package com.crms.model;

public class Witness {
    private int id;
    private String firNumber;
    private String firstName;
    private String lastName;
    private String contact;
    private String statement;

    public Witness() {}

    public Witness(int id, String firNumber, String firstName, String lastName, String contact, String statement) {
        this.id = id;
        this.firNumber = firNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contact = contact;
        this.statement = statement;
    }

    // Getters and setters
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