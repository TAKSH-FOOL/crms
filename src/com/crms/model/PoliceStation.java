package com.crms.model;

public class PoliceStation {
    private int id;
    private String stationName;
    private String address;
    private String phone;
    private boolean isActive;

    public PoliceStation() {}

    public PoliceStation(int id, String stationName, String address, String phone, boolean isActive) {
        this.id = id;
        this.stationName = stationName;
        this.address = address;
        this.phone = phone;
        this.isActive = isActive;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "PoliceStation{" +
                "id=" + id +
                ", stationName='" + stationName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}