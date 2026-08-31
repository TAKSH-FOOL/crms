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

    public static String getAdminType(User user) {
        if (user == null || !"ADMIN".equals(user.getRole())) return null;
        Admin admin = AdminDAO.getByUserId(user.getId());
        return admin != null ? admin.getDesignation() : null;
    }

    public static boolean isAnyAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }

}