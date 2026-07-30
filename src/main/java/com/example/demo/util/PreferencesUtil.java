package com.example.demo.util;

import java.util.prefs.Preferences;

public class PreferencesUtil {
    private static final Preferences prefs = Preferences.userRoot().node("expensetracker");

    public static boolean isDarkModeEnabled() {
        return prefs.getBoolean("dark_mode", false);
    }

    public static void setDarkModeEnabled(boolean enabled) {
        prefs.putBoolean("dark_mode", enabled);
    }
}