package com.example.gamelink.models;

import java.util.List;

public class User {
    private String userId;
    private String name;
    private int age;
    private String country;
    private List<String> favoriteGames;
    private List<String> friends;
    private String profileImageUrl;

    public User() {}

    public User(String userId, String name, int age, String country, List<String> favoriteGames) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.country = country;
        this.favoriteGames = favoriteGames;
    }

    public User(String userId, String name, int age, String country, List<String> favoriteGames, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.country = country;
        this.favoriteGames = favoriteGames;
        this.profileImageUrl=profileImageUrl;
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

    public String getNickname() {
        return name;
    }

    public void setNickname(String nickname) {
        this.name = nickname;
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

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
