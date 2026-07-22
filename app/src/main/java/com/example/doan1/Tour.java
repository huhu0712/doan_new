package com.example.doan1;

import java.io.Serializable;

public class Tour implements Serializable {
    private String title;
    private String imageUrl;
    private String rating;
    private String duration;
    private String price;

    public Tour(String title, String imageUrl, String rating, String duration, String price) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.duration = duration;
        this.price = price;
    }

    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public String getRating() { return rating; }
    public String getDuration() { return duration; }
    public String getPrice() { return price; }
}
