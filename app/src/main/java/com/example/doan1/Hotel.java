package com.example.doan1;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Hotel implements Serializable {
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

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    private String airbnbUrl;

    // Getters
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public double getRating() { return rating; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getAirbnbUrl() { return airbnbUrl; }

    // Setters (Dùng cho trường hợp cần cập nhật thủ công)
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setAddress(String address) { this.address = address; }
    public void setRating(double rating) { this.rating = rating; }
    public void setAirbnbUrl(String airbnbUrl) { this.airbnbUrl = airbnbUrl; }
}
