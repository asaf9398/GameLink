package com.example.gamelink.utils;

import com.example.gamelink.models.Rating;

import java.util.List;

public class RatingUtils {

    public static float calculateAverageRating(List<Rating> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return 0;
        }

        int totalStars = 0;
        for (Rating rating : ratings) {
            totalStars += rating.getStars();
        }

        return (float) totalStars / ratings.size();
    }

    public static String formatRating(float rating) {
        return String.format("%.1f", rating);
    }
}
