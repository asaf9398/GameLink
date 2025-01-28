package com.example.gamelink.models;

public class Game {
    private String gameId;
    private String gameName;

    public Game() {
    }

    public Game(String gameId, String gameName) {
        this.gameId = gameId;
        this.gameName = gameName;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
