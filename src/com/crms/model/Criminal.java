package com.crms.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Criminal {
     int id;
     String firstName;
     String lastName;
     LocalDate dob;
     String gender;
     String address;
     String phone;
     String wantedStatus;   // WANTED, NOT_WANTED
     boolean isActive;

    public Criminal() {}


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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("┌──────────────────────────────────────────────────────────┐\n");
        sb.append("│                    CRIMINAL DETAILS                      │\n");
        sb.append("├──────────────────────────────────────────────────────────┤\n");
        sb.append(String.format("%-20s : %-40s \n", "ID", id));
        sb.append(String.format("%-20s : %-40s \n", "First Name", firstName != null ? firstName : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Last Name", lastName != null ? lastName : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Date of Birth",
                dob != null ? dob.format(formatter) : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Gender", gender != null ? gender : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Address", address != null ? address : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Phone", phone != null ? phone : "N/A"));
        sb.append(String.format("%-20s : %-40s \n", "Wanted Status",
                wantedStatus != null ? wantedStatus : "N/A"));
        sb.append("└──────────────────────────────────────────────────────────┘");
        return sb.toString();
    }
}