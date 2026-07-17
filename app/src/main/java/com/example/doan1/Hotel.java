package com.example.doan1;

import com.google.gson.annotations.SerializedName;

public class Hotel {
    @SerializedName("hotel_name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("min_total_price")
    private double price;

    @SerializedName("main_photo_url")
    private String imageUrl;

    @SerializedName("review_score")
    private double rating;

    // Getters and Setters
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public double getRating() { return rating; }
}
