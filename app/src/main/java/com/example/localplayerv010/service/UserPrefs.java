package com.example.localplayerv010.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPrefs {
    private static final String PREFS_NAME = "user_prefs";

    public static void saveUserInfo(Context context, int userId, String username, String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("user_id", userId);
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean("is_logged_in", false);
    }

    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public static int getCurrentUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    public static String getCurrentUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("username", "");
    }

    public static String getCurrentEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("email", "");
    }
}
