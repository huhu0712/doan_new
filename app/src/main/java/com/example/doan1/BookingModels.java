package com.example.doan1;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookingModels {

    // Model cho kết quả tìm kiếm địa điểm
    public static class LocationResponse {
        @SerializedName("status")
        public boolean status;
        @SerializedName("message")
        public String message;
        @SerializedName("data")
        public List<JsonElement> data;
    }

    public static class LocationItem {
        @SerializedName("dest_id")
        public String destId;
        
        @SerializedName("dest_type")
        public String destType;
        
        @SerializedName("name")
        public String name;
        
        @SerializedName("label")
        public String label;
    }

    // Model cho phản hồi danh sách khách sạn
    public static class BookingResponse {
        @SerializedName("result")
        public List<HotelItem> result;
    }

    // Model chi tiết từng khách sạn
    public static class HotelItem {
        @SerializedName("hotel_name")
        public String hotelName;

        @SerializedName("main_photo_url")
        public String mainPhotoUrl;

        @SerializedName("review_score")
        public double reviewScore;

        @SerializedName("currency_code")
        public String currencyCode;

        @SerializedName("composite_price_breakdown")
        public PriceBreakdown priceBreakdown;
        
        @SerializedName("type")
        public String type; // Dùng để lọc bỏ các quảng cáo/banner
        
        @SerializedName("address")
        public String address;

        @SerializedName("latitude")
        public double latitude;

        @SerializedName("longitude")
        public double longitude;
    }

    public static class PriceBreakdown {
        @SerializedName("gross_amount")
        public Amount grossAmount;
    }

    public static class Amount {
        @SerializedName("amount_rounded")
        public String amountRounded;
        
        @SerializedName("value")
        public double value;
    }
}
