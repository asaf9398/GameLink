package com.example.gamelink.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public class LanguageManager {

    public static String getCurrentLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(Constants.LANGUAGE_KEY, Constants.DEFAULT_LANGUAGE);
    }

    public static void setLanguage(Context context, String languageCode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.LANGUAGE_KEY, languageCode);
        editor.apply();

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        android.content.res.Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}

