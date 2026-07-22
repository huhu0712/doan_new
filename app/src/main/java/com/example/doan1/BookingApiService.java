package com.example.doan1;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface BookingApiService {
    
    /**
     * API 1: Tìm kiếm mã vùng theo chuẩn DataCrawler (api/v1/meta/getLocations)
     */
    @GET("api/v1/meta/getLocations")
    Call<BookingModels.LocationResponse> searchDestination(
        @Header("X-RapidAPI-Key") String apiKey,
        @Header("X-RapidAPI-Host") String apiHost,
        @Query("query") String text,
        @Query("languagecode") String locale
    );

    /**
     * API 2: Lấy danh sách khách sạn theo chuẩn DataCrawler (api/v1/hotels/searchHotels)
     */
    @GET("api/v1/hotels/searchHotels")
    Call<BookingModels.BookingResponse> getHotels(
        @Header("X-RapidAPI-Key") String apiKey,
        @Header("X-RapidAPI-Host") String apiHost,
        @Query("dest_id") String destId, // DataCrawler dùng số ít dest_id
        @Query("arrival_date") String arrivalDate,
        @Query("departure_date") String departureDate,
        @Query("adults") int guestQty, // DataCrawler dùng adults thay vì guest_qty
        @Query("room_qty") int roomQty,
        @Query("units") String units,
        @Query("currency_code") String currency,
        @Query("languagecode") String locale,
        @Query("sort_by") String sortBy,
        @Query("categories_filter_ids") String categoriesFilter,
        @Query("price_min") Integer priceMin,
        @Query("price_max") Integer priceMax
    );
}
