package com.crms.util;

import com.crms.dao.PoliceStationDAO;
import com.crms.model.FIR;
import com.crms.model.PoliceStation;

import java.time.format.DateTimeFormatter;

public class ReportGenerator {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static String generateFIRReport(FIR fir) {
        if (fir == null) return null;

        StringBuilder sb = new StringBuilder();
        sb.append("======================================================\n");
        sb.append("              FIRST INFORMATION REPORT (FIR)          \n");
        sb.append("======================================================\n\n");
        sb.append("FIR Number         : ").append(nullSafe(fir.getFirNumber())).append("\n");
        sb.append("Complainant        : ").append(nullSafe(fir.getComplainantName())).append("\n");
        sb.append("Contact            : ").append(nullSafe(fir.getComplainantContact())).append("\n");
        sb.append("Incident Location  : ").append(nullSafe(fir.getIncidentLocation())).append("\n");
        sb.append("Incident Date      : ").append(fir.getIncidentDate() != null ?
                fir.getIncidentDate().format(DATE_FORMATTER) : "N/A").append("\n");
        sb.append("Description        : ").append(nullSafe(fir.getIncidentDescription())).append("\n");
        sb.append("Crime Category     : ").append(nullSafe(fir.getCrimeCategory())).append("\n");
        sb.append("Status             : ").append(nullSafe(fir.getStatus())).append("\n");
        sb.append("Station            : ").append(nullSafe(PoliceStationDAO.getStationNameById(fir.getStationId()))).append("\n");
        sb.append("Assigned Officer   : ").append(fir.getAssignedOfficerId() != null ?
                "ID: " + fir.getAssignedOfficerId() : "None").append("\n");
        sb.append("Created At         : ").append(fir.getCreatedAt() != null ?
                fir.getCreatedAt().format(DATE_FORMATTER) : "N/A").append("\n");
        sb.append("======================================================\n");

        return sb.toString();
    }

    private static String nullSafe(String s) {
        return s != null ? s : "N/A";
    }
}