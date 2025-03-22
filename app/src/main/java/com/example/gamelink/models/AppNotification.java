package com.example.gamelink.models;

public class AppNotification {
    private String id;
    private String message;
    private String senderId;
    private long timestamp;
    private boolean read;

    public AppNotification() {} // חובה ל-Firebase

    public AppNotification(String id, String message, String senderId, long timestamp, boolean read) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.read = read;
    }

    // Getters
    public String getId() { return id; }
    public String getMessage() { return message; }
    public String getSenderId() { return senderId; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return read; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setMessage(String message) { this.message = message; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setRead(boolean read) { this.read = read; }
}
