package com.example.gamelink.models;

public class Recommendation {
    private String recommendedId;
    private String reason;

    public Recommendation() {
    }

    public Recommendation(String recommendedId, String reason) {
        this.recommendedId = recommendedId;
        this.reason = reason;
    }

    public String getRecommendedId() {
        return recommendedId;
    }

    public void setRecommendedId(String recommendedId) {
        this.recommendedId = recommendedId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

