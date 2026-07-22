package com.example.doan1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface TravelApiService {

    @GET("auto-complete")
    Call<TravelModels.AutoCompleteResponse> getAutoComplete(
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String host,
            @Query("query") String query
    );

    @GET("attractions/search")
    Call<TravelModels.AttractionSearchResponse> searchAttractions(
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String host,
            @Query("geoId") String geoId,
            @Query("units") String units,
            @Query("sortType") String sortType
    );

    @GET("attractions/details")
    Call<TravelModels.AttractionDetailResponse> getAttractionDetails(
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String host,
            @Query("contentId") String contentId,
            @Query("units") String units
    );

    @GET("attractions/reviews")
    Call<TravelModels.ReviewResponse> getAttractionReviews(
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String host,
            @Query("contentId") String contentId,
            @Query("units") String units
    );
}
