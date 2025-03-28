package com.example.gamelink.firebase;

import com.example.gamelink.models.Game;
import com.example.gamelink.models.User;

import java.util.ArrayList;
import java.util.List;

public class MatchingAlgorithm {

    // Match users based on preferences (e.g., game, age, country)
    public List<User> matchUsers(List<User> allUsers, String game, int minAge, int maxAge, String country) {
        List<User> matchedUsers = new ArrayList<>();

        for (User user : allUsers) {
            // Match criteria: favorite game, age range, and country
            if (user.getFavoriteGames().contains(game) &&
                    user.getAge() >= minAge &&
                    user.getAge() <= maxAge &&
                    user.getCountry().equalsIgnoreCase(country)) {
                matchedUsers.add(user);
            }
        }

        return matchedUsers;
    }

    // Calculate match percentage between two users
    public int calculateMatchPercentage(User user1, User user2) {
        int score = 0;

        // Check for matching favorite games
        for (String game : user1.getFavoriteGames()) {
            if (user2.getFavoriteGames().contains(game)) {
                score += 20; // +20 points per matching game
            }
        }

        // Check for matching country
        if (user1.getCountry().equalsIgnoreCase(user2.getCountry())) {
            score += 30;
        }

        // Check for similar age range
        int ageDifference = Math.abs(user1.getAge() - user2.getAge());
        if (ageDifference <= 5) {
            score += 50; // +50 points if ages are within 5 years
        }

        return score; // Max score = 100
    }
}

