package com.example.doan1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvDestinations;
    private DestinationAdapter adapter;
    private List<RSSFeed.RSSItem> vnItems = new ArrayList<>();
    private View btnHotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ View an toàn
        rvDestinations = findViewById(R.id.rvDestinations);
        btnHotel = findViewById(R.id.btnHotel);
        View btnApartment = findViewById(R.id.btnApartment);
        View btnFlight = findViewById(R.id.btnFlight);
        View searchBar = findViewById(R.id.searchBar);

        // Kiểm tra null để tránh văng app
        if (btnHotel != null) {
            btnHotel.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HotelActivity.class)));
        }

        if (btnApartment != null) {
            btnApartment.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ApartmentActivity.class)));
        }

        View btnCarRental = findViewById(R.id.btnCarRental);
        if (btnCarRental != null) {
            btnCarRental.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CarRentalActivity.class)));
        }

        View btnTour = findViewById(R.id.btnTour);
        if (btnTour != null) {
            btnTour.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TourActivity.class)));
        }

        if (btnFlight != null) {
            btnFlight.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FlightActivity.class)));
        }

        if (searchBar != null) {
            searchBar.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
        }

        // Cấu hình RecyclerView - Tắt NestedScrolling để cuộn mượt trong NestedScrollView
        if (rvDestinations != null) {
            rvDestinations.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
            rvDestinations.setNestedScrollingEnabled(false);
            adapter = new DestinationAdapter(this, vnItems);
            rvDestinations.setAdapter(adapter);
        }

        // Cấu hình Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_home); // Đặt mặc định là Trang chủ
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_ai_assistant) {
                startActivity(new Intent(MainActivity.this, AiAssistantActivity.class));
                return true;
            } else if (id == R.id.nav_login) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
            }
            return true;
        });

        // Gọi API lấy tin tức du lịch thật từ VNExpress
        fetchVnExpressNews();
    }

    private void fetchVnExpressNews() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://vnexpress.net/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        RssService rssService = retrofit.create(RssService.class);

        rssService.getTravelNews().enqueue(new Callback<RSSFeed>() {
            @Override
            public void onResponse(Call<RSSFeed> call, Response<RSSFeed> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RSSFeed.RSSItem> allItems = response.body().getItems();
                    
                    if (allItems != null) {
                        vnItems.clear();
                        for (RSSFeed.RSSItem item : allItems) {
                            if (isVietNamDestination(item.getTitle(), item.getCleanDescription())) {
                                vnItems.add(item);
                            }
                        }

                        Log.d("RSS_DEBUG", "Số lượng tin Việt Nam: " + vnItems.size());
                        
                        // Cập nhật adapter
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<RSSFeed> call, Throwable t) {
                Log.e("RSS_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }

    private boolean isVietNamDestination(String title, String desc) {
        if (title == null || desc == null) return false;
        String content = (title + " " + desc).toLowerCase();

        // 1. Danh sách từ khóa loại trừ (Quốc tế)
        String[] blackList = {
            "thái lan", "nhật bản", "hàn quốc", "trung quốc", "mỹ", "pháp", "đức", "anh", 
            "singapore", "malaysia", "bali", "châu âu", "châu á", "quốc tế", "nước ngoài",
            "thế giới", "paris", "tokyo", "seoul", "bangkok", "phuket", "dubai", "nga", "úc"
        };

        for (String blackKey : blackList) {
            if (content.contains(blackKey)) return false;
        }

        // 2. Danh sách từ khóa bắt buộc về địa danh Việt Nam (Mở rộng để lấy nhiều tin hơn)
        String[] whiteList = {
            "việt nam", "hà nội", "đà nẵng", "hồ chí minh", "sài gòn", "nha trang", 
            "đà lạt", "phú quốc", "vũng tàu", "hạ long", "hội an", "sa pa", "sapa",
            "huế", "ninh bình", "quảng bình", "quy nhơn", "phú yên", "miền tây", 
            "cần thơ", "hà giang", "mộc châu", "an giang", "kiên giang", "phan thiết",
            "du lịch", "ẩm thực", "điểm đến", "trải nghiệm", "vùng cao", "biển", "phố cổ"
        };
        
        for (String whiteKey : whiteList) {
            if (content.contains(whiteKey)) return true;
        }

        return false;
    }
}
