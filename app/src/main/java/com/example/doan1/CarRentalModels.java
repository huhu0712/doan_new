package com.example.doan1;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CarRentalModels {

    // Models for auto-complete
    public static class AutoCompleteResponse {
        @SerializedName("status")
        public boolean status;
        @SerializedName("data")
        public List<LocationData> data;
    }

    public static class LocationData {
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("type")
        public String type; // e.g., "AIRPORT", "CITY"
    }

    // Models for car search
    public static class CarSearchResponse {
        @SerializedName("status")
        public boolean status;
        @SerializedName("data")
        public SearchData data;
    }

    public static class SearchData {
        @SerializedName("car_rentals")
        public List<CarRental> carRentals;
    }

    public static class CarRental {
        @SerializedName("vehicle_info")
        public VehicleInfo vehicleInfo;
        @SerializedName("pricing_info")
        public PricingInfo pricingInfo;
        @SerializedName("supplier_info")
        public SupplierInfo supplierInfo;
    }

    public static class VehicleInfo {
        @SerializedName("v_name")
        public String vName;
        @SerializedName("image_url")
        public String imageUrl;
        @SerializedName("seats")
        public String seats;
        @SerializedName("transmission")
        public String transmission;
    }

    public static class PricingInfo {
        @SerializedName("price")
        public double price;
        @SerializedName("currency")
        public String currency;
    }

    public static class SupplierInfo {
        @SerializedName("name")
        public String name;
    }
}
