package com.example.doan1;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RssService {
    @GET("rss/du-lich.rss")
    Call<RSSFeed> getTravelNews();
}
