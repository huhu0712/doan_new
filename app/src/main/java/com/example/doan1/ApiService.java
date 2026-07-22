package com.example.doan1;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {
    @GET("properties/list")
    Call<HotelResponse> getHotels(
        @Header("X-RapidAPI-Key") String apiKey,
        @Header("X-RapidAPI-Host") String apiHost,
        @Query("dest_type") String destType,
        @Query("dest_id") String destId,
        @Query("arrival_date") String arrivalDate,   // Theo đúng hình bạn chụp
        @Query("departure_date") String departureDate, // Theo đúng hình bạn chụp
        @Query("guest_qty") int guestQty,             // Theo đúng hình bạn chụp
        @Query("room_qty") int roomQty,               // Theo đúng hình bạn chụp
        @Query("order_by") String orderBy,
        @Query("units") String units,
        @Query("locale") String locale
    );

    @GET("dev-huydq/mock-api/destinations")
    Call<List<Destination>> getDestinations();
}
