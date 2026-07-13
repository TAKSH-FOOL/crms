package com.crms.model;

public class Evidence {
    private int id;
    private String evidenceNumber;
    private String firNumber;
    private String crimeNumber;
    private String type;
    private String description;
    private String custodian;
    private boolean isActive;

    public Evidence() {}

    public Evidence(int id, String evidenceNumber, String firNumber, String crimeNumber,
                    String type, String description, String custodian, boolean isActive) {
        this.id = id;
        this.evidenceNumber = evidenceNumber;
        this.firNumber = firNumber;
        this.crimeNumber = crimeNumber;
        this.type = type;
        this.description = description;
        this.custodian = custodian;
        this.isActive = isActive;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEvidenceNumber() { return evidenceNumber; }
    public void setEvidenceNumber(String evidenceNumber) { this.evidenceNumber = evidenceNumber; }
    public String getFirNumber() { return firNumber; }
    public void setFirNumber(String firNumber) { this.firNumber = firNumber; }
    public String getCrimeNumber() { return crimeNumber; }
    public void setCrimeNumber(String crimeNumber) { this.crimeNumber = crimeNumber; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCustodian() { return custodian; }
    public void setCustodian(String custodian) { this.custodian = custodian; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "Evidence{" +
                "id=" + id +
                ", evidenceNumber='" + evidenceNumber + '\'' +
                ", type='" + type + '\'' +
                ", custodian='" + custodian + '\'' +
                '}';
    }
}