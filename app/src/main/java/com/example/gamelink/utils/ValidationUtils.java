package com.example.gamelink.utils;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6; // לדוגמה: מינימום 6 תווים
    }

    public static boolean isValidName(String name) {
        return name != null && name.trim().length() > 0;
    }

    public static boolean isValidAge(int age) {
        return age >= 13 && age <= 100; // לדוגמה: מגבלת גיל
    }
}
