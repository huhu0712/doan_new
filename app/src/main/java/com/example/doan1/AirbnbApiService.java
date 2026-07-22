package com.example.doan1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface AirbnbApiService {
    @GET("common/auto-complete")
    Call<AirbnbModels.AutoCompleteResponse> autoComplete(
        @Header("x-rapidapi-key") String apiKey,
        @Header("x-rapidapi-host") String apiHost,
        @Query("query") String query
    );

    @GET("stays/search")
    Call<AirbnbModels.AirbnbResponse> searchByLocation(
        @Header("x-rapidapi-key") String apiKey,
        @Header("x-rapidapi-host") String apiHost,
        @Query("placeId") String placeId,
        @Query("checkin") String checkin,
        @Query("checkout") String checkout,
        @Query("adults") Integer adults,
        @Query("children") Integer children,
        @Query("infants") Integer infants,
        @Query("pets") Integer pets,
        @Query("min_bedrooms") Integer minBedrooms,
        @Query("category_tag") String categoryTag,
        @Query("currency") String currency,
        @Query("locale") String locale
    );

    @GET("stays/detail")
    Call<AirbnbModels.DetailResponse> getPropertyDetail(
        @Header("x-rapidapi-key") String apiKey,
        @Header("x-rapidapi-host") String apiHost,
        @Query("listingId") String listingId,
        @Query("locale") String locale
    );

    @GET("property/reviews")
    Call<AirbnbModels.ReviewResponse> getPropertyReviews(
        @Header("x-rapidapi-key") String apiKey,
        @Header("x-rapidapi-host") String apiHost,
        @Query("id") String listingId,
        @Query("locale") String locale
    );
}
