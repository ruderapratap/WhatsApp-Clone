package com.ruderarajput.whatsapp.Model;

public class Status {
    private String imageUrl;
    private long timestamp;

    public Status() {
        // Default constructor required for Firebase
    }

    public Status(String imageUrl, long timestamp) {
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
