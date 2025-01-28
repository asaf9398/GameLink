package com.example.gamelink.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class DarkModeManager {

    public static boolean isDarkModeEnabled(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(Constants.DARK_MODE_KEY, false);
    }

    public static void setDarkMode(Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Constants.DARK_MODE_KEY, enabled);
        editor.apply();

        AppCompatDelegate.setDefaultNightMode(enabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}

