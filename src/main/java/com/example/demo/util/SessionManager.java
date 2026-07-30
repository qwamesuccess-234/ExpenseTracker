package com.example.demo.util;

import com.example.demo.model.Expense;
import com.example.demo.model.User;

public class SessionManager {
    public static User currentUser;
    private static Expense selectedExpense;

    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }

    public static void setSelectedExpense(Expense expense) { selectedExpense = expense; }
    public static Expense getSelectedExpense() { return selectedExpense; }

    // New: type-check helpers, single source of truth for the whole team
    public static boolean isIndividual() {
        return currentUser != null && "Individual".equals(currentUser.getUserType());
    }

    public static boolean isCorporation() {
        return currentUser != null && "Corporation".equals(currentUser.getUserType());
    }

    public static boolean isEnterprise() {
        return currentUser != null && "Enterprise".equals(currentUser.getUserType());
    }

    public static int getOrganizationOwnerId() {
        User user = getCurrentUser();
        if (user == null) return -1;
        return user.getOrganizationId() != null ? user.getOrganizationId() : user.getId();
    }

    public static boolean isBusinessAccount() {
        return isCorporation() || isEnterprise();
    }

    public static void clear() {
        currentUser = null;
        selectedExpense = null;
    }
}