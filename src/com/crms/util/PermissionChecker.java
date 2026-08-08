package com.crms.util;

import com.crms.model.Admin;
import com.crms.model.User;
import com.crms.dao.AdminDAO;

public class PermissionChecker {

    public static boolean isSuperAdmin(User user) {
        if (user == null || !"ADMIN".equals(user.getRole())) return false;
        Admin admin = AdminDAO.getByUserId(user.getId());
        return admin != null && admin.isSuperAdmin();
    }

    public static boolean isStationAdmin(User user) {
        if (user == null || !"ADMIN".equals(user.getRole())) return false;
        Admin admin = AdminDAO.getByUserId(user.getId());
        return admin != null && !admin.isSuperAdmin();
    }

    /**
     * Returns the designation of the admin user, or null if not an admin or record not found.
     * @param user the user to check
     * @return "Super Admin", "Station Admin", or null
     */
    public static String getAdminType(User user) {
        if (user == null || !"ADMIN".equals(user.getRole())) return null;
        Admin admin = AdminDAO.getByUserId(user.getId());
        return admin != null ? admin.getDesignation() : null;
    }

    /**
     * Checks if the user is any type of admin (Super or Station).
     * Equivalent to checking user.getRole().equals("ADMIN").
     */
    public static boolean isAnyAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }

}