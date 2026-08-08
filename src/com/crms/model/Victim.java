package com.crms.model;

public class Victim {
     int id;
     String firNumber;
     String firstName;
     String lastName;
     String contact;
     String address;
     boolean isActive;
     String harm_description;

    public Victim() {}

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
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }

    public String getHarm_description() {
        return harm_description;
    }

    public void setHarm_description(String harm_description) {
        this.harm_description = harm_description;
    }

    @Override
    public String toString() {
        return "Victim{" +
                "id=" + id +
                ", firNumber='" + firNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}