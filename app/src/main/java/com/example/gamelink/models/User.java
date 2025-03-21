package com.example.gamelink.models;

import java.util.List;

public class User {
    private String userId;
    private String name;
    private String nickname;   // *** ADDED: כינוי
    private int age;
    private String country;
    private List<String> favoriteGames;

    public User() {
        // Default constructor required for Firebase
    }

    // *** Constructor מלא כולל nickname
    public User(String userId, String name, String nickname, int age, String country, List<String> favoriteGames) {
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
        this.age = age;
        this.country = country;
        this.favoriteGames = favoriteGames;
    }

    // אפשר להשאיר גם בנאי "ישן" אם אתם רוצים, אבל מומלץ לאחד ולוודא שקוד יצירת user משתמש בבנאי החדש.

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

    // *** GET/SET nickname
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
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
