package com.example.gamelink.models;

import java.util.List;

public class User {
    private String userId;
    private String name;
    private int age;
    private String country;
    private List<String> favoriteGames;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String userId, String name, int age, String country, List<String> favoriteGames) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.country = country;
        this.favoriteGames = favoriteGames;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getFavoriteGames() {
        return favoriteGames;
    }

    public void setFavoriteGames(List<String> favoriteGames) {
        this.favoriteGames = favoriteGames;
    }
}
