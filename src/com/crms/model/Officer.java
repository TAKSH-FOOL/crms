package com.crms.model;

public class Officer {
     int id;
     int userId;
     String badgeNumber;
     String officerRank;  // renamed from 'rank'
     int stationId;

    public Officer() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getBadgeNumber() { return badgeNumber; }
    public void setBadgeNumber(String badgeNumber) { this.badgeNumber = badgeNumber; }
    public String getOfficerRank() { return officerRank; }
    public void setOfficerRank(String officerRank) { this.officerRank = officerRank; }
    public int getStationId() { return stationId; }
    public void setStationId(int stationId) { this.stationId = stationId; }

    @Override
    public String toString() {
        return "Officer{" +
                "id=" + id +
                ", userId=" + userId +
                ", badgeNumber='" + badgeNumber + '\'' +
                ", officerRank='" + officerRank + '\'' +
                '}';
    }
}