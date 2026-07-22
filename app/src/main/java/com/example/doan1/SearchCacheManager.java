package com.example.doan1;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchCacheManager {
    private static final String PREF_NAME = "ApartmentSearchCache";
    private static final long CACHE_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L; // 7 ngày tính bằng milliseconds

    private static class CacheWrapper {
        long timestamp;
        List<AirbnbModels.StaySearchResult> results;

        CacheWrapper(List<AirbnbModels.StaySearchResult> results) {
            this.timestamp = System.currentTimeMillis();
            this.results = results;
        }
    }

    public static void saveCache(Context context, String location, List<AirbnbModels.StaySearchResult> results) {
        if (location == null || results == null) return;
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        CacheWrapper wrapper = new CacheWrapper(results);
        String json = gson.toJson(wrapper);
        
        prefs.edit().putString(location.toLowerCase().trim(), json).apply();
    }

    public static List<AirbnbModels.StaySearchResult> getCachedResults(Context context, String location) {
        if (location == null) return null;
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(location.toLowerCase().trim(), null);
        
        if (json == null) return null;
        
        Gson gson = new Gson();
        CacheWrapper wrapper = gson.fromJson(json, CacheWrapper.class);
        
        if (wrapper == null) return null;
        
        // Kiểm tra hết hạn
        if (System.currentTimeMillis() - wrapper.timestamp > CACHE_EXPIRATION_TIME) {
            prefs.edit().remove(location.toLowerCase().trim()).apply();
            return null;
        }
        
        return wrapper.results;
    }
}
