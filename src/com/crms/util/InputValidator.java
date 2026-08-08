package com.crms.util;

import javax.swing.plaf.PanelUI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InputValidator {
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.length() != 10) {
            return false;
        }

        if (phone.charAt(0) < '6' || phone.charAt(0) > '9') {
            return false;
        }

        for (int i = 0; i < phone.length(); i++) {
            if (!Character.isDigit(phone.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static boolean isValidDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate parsed = LocalDate.parse(date.trim(), DATE_FORMATTER);
            // Reject future dates
            return !parsed.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return name.matches("[a-zA-Z\\s]+");
    }

    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        return address.matches("[a-zA-Z0-9\\s,.-]+");
    }

    public static boolean isValidFIRNumber(String firNumber){
        if (firNumber.matches("^FIR-[A-Za-z0-9]+-[0-9]+$"))
            return true;
        else
            return false;
    }

    public static boolean isValidGender(String gender) {

        if (gender == null || gender.trim().isEmpty()) {
            return false;
        }
        return gender.matches("MALE|FEMALE|OTHER");
    }

    public static boolean isValidCrimeNumber(String crimeNumber) {
        if (crimeNumber == null || crimeNumber.trim().isEmpty()) {
            return false;
        }
        return crimeNumber.matches("^CN-[A-Za-z0-9]+-[0-9]+-[0-9]+$");
    }

    /**
     * Validates a date‑time string in format "yyyy-MM-dd HH:mm:ss".
     * @param dateTime the string to validate
     * @return true if the string is a valid date‑time in the correct format
     */
    public static boolean isValidDateTime(String dateTime) {
        if (dateTime == null || dateTime.trim().isEmpty()) return false;
        try {
            LocalDateTime dt = LocalDateTime.parse(dateTime.trim(),
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            // Reject future dates
            return !dt.isAfter(LocalDateTime.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}