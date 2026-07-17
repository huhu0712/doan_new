package com.example.doan1;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HotelResponse {
    @SerializedName("result")
    private List<Hotel> hotels;

    public List<Hotel> getHotels() {
        return hotels;
    }
}
