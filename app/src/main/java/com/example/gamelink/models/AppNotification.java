package com.example.gamelink.models;

public class AppNotification {
    private String id;
    private String senderId;
    private String message;
    private long timestamp;
    private boolean read;

    public AppNotification() {}

    public AppNotification(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public AppNotification(String id, String senderId, String message, long timestamp, boolean read) {
        this.id = id;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
