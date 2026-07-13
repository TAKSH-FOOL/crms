package com.crms.model;

public class Officer {
    private int id;
    private int userId;
    private String badgeNumber;
    private String officerRank;  // renamed from 'rank'

    public Officer() {}

    public Officer(int id, int userId, String badgeNumber, String officerRank) {
        this.id = id;
        this.userId = userId;
        this.badgeNumber = badgeNumber;
        this.officerRank = officerRank;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getBadgeNumber() { return badgeNumber; }
    public void setBadgeNumber(String badgeNumber) { this.badgeNumber = badgeNumber; }
    public String getOfficerRank() { return officerRank; }
    public void setOfficerRank(String officerRank) { this.officerRank = officerRank; }

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