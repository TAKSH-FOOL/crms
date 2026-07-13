package com.crms.model;

import java.time.LocalDate;

public class Criminal {
    private int id;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private String address;
    private String phone;
    private String wantedStatus;   // WANTED, NOT_WANTED
    private boolean isActive;

    public Criminal() {}

    public Criminal(int id, String firstName, String lastName, LocalDate dob, String gender,
                    String address, String phone, String wantedStatus, boolean isActive) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.wantedStatus = wantedStatus;
        this.isActive = isActive;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getWantedStatus() { return wantedStatus; }
    public void setWantedStatus(String wantedStatus) { this.wantedStatus = wantedStatus; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "Criminal{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", wantedStatus='" + wantedStatus + '\'' +
                '}';
    }
}