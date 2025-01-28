package com.example.gamelink.models;

public class Rating {
    private String userId;
    private int stars;
    private String feedback;

    public Rating() {
    }

    public Rating(String userId, int stars, String feedback) {
        this.userId = userId;
        this.stars = stars;
        this.feedback = feedback;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}

