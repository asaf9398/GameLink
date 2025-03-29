package com.example.gamelink.firebase;

import com.example.gamelink.models.Game;
import com.example.gamelink.models.User;

import java.util.ArrayList;
import java.util.List;

public class MatchingAlgorithm {

    public List<User> matchUsers(List<User> allUsers, String game, int minAge, int maxAge, String country) {
        List<User> matchedUsers = new ArrayList<>();

        for (User user : allUsers) {
            if (user.getFavoriteGames().contains(game) &&
                    user.getAge() >= minAge &&
                    user.getAge() <= maxAge &&
                    user.getCountry().equalsIgnoreCase(country)) {
                matchedUsers.add(user);
            }
        }

        return matchedUsers;
    }

    public int calculateMatchPercentage(User user1, User user2) {
        int score = 0;

        for (String game : user1.getFavoriteGames()) {
            if (user2.getFavoriteGames().contains(game)) {
                score += 20;
            }
        }

        if (user1.getCountry().equalsIgnoreCase(user2.getCountry())) {
            score += 30;
        }

        int ageDifference = Math.abs(user1.getAge() - user2.getAge());
        if (ageDifference <= 5) {
            score += 50;
        }

        return score;
    }
}

