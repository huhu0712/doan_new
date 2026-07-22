package com.example.doan1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface CarRentalApiService {
    @GET("car/auto-complete")
    Call<CarRentalModels.AutoCompleteResponse> autoComplete(
        @Header("x-rapidapi-key") String apiKey,
        @Header("x-rapidapi-host") String apiHost,
        @Query("query") String query
    );

    @GET("car/search")
    Call<CarRentalModels.CarSearchResponse> searchCars(
        @Header("x-rapidapi-key") String apiKey,
        @Header("x-rapidapi-host") String apiHost,
        @Query("pickUpId") String pickUpId,
        @Query("dropOffId") String dropOffId, // Thêm dropOffId
        @Query("pickUpDate") String pickUpDate,
        @Query("pickUpTime") String pickUpTime,
        @Query("dropOffDate") String dropOffDate,
        @Query("dropOffTime") String dropOffTime,
        @Query("currency") String currency
    );
}
