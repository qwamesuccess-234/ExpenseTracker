package com.example.demo.util;

import com.example.demo.model.Expense;
import com.example.demo.model.User;

public class SessionManager {
    private static User currentUser;
    private static Expense selectedExpense;

    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }

    public static void setSelectedExpense(Expense expense) { selectedExpense = expense; }
    public static Expense getSelectedExpense() { return selectedExpense; }

    public static void clear() {
        currentUser = null;
        selectedExpense = null;
    }
}