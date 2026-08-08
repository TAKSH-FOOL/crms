package com.crms.model;

public class PoliceStation {
     int id;
     String stationName;
     String address;
     String phone;
     boolean isActive;
     String stationCode;

    public PoliceStation() {}


    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStationCode() { return stationCode; }
    public void setStationCode(String stationCode) { this.stationCode = stationCode; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "PoliceStation{" +
                "id=" + id +
                ", stationName='" + stationName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", stationCode='" + stationCode + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}