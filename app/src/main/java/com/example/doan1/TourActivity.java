package com.example.doan1;

import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TourActivity extends AppCompatActivity {

    private RecyclerView rvTours;
    private TourAdapter adapter;
    private List<TravelModels.AttractionCard> attractionList = new ArrayList<>();
    private TabLayout tabCategories;
    private TravelApiService apiService;
    private EditText etSearchTour;

    private static final String API_KEY = "d0dafa9f25msh35fc9ba30aff965p1e8538jsna97ae9bf3eb4";
    private static final String API_HOST = "tripadvisor-com1.p.rapidapi.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        ImageButton btnBack = findViewById(R.id.btnBackTour);
        btnBack.setOnClickListener(v -> finish());

        rvTours = findViewById(R.id.rvTours);
        tabCategories = findViewById(R.id.tabTourCategories);
        etSearchTour = findViewById(R.id.etSearchTour);

        setupRetrofit();
        setupEvents();

        adapter = new TourAdapter(attractionList);
        rvTours.setLayoutManager(new LinearLayoutManager(this));
        rvTours.setAdapter(adapter);

        // Mặc định tìm kiếm tour tại Hà Nội
        searchGeoId("Hanoi");

        tabCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(TourActivity.this, "Đang lọc: " + tab.getText(), Toast.LENGTH_SHORT).show();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupEvents() {
        etSearchTour.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String query = etSearchTour.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchGeoId(query);
                }
                return true;
            }
            return false;
        });

        findViewById(R.id.chipHanoi).setOnClickListener(v -> {
            etSearchTour.setText("Hanoi");
            searchGeoId("Hanoi");
        });
        findViewById(R.id.chipHCM).setOnClickListener(v -> {
            etSearchTour.setText("Ho Chi Minh City");
            searchGeoId("Ho Chi Minh City");
        });
        findViewById(R.id.chipDanang).setOnClickListener(v -> {
            etSearchTour.setText("Da Nang");
            searchGeoId("Da Nang");
        });

        findViewById(R.id.btnSortTour).setOnClickListener(v -> 
            Toast.makeText(this, "Tính năng sắp xếp đang được cập nhật", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnFilterTour).setOnClickListener(v -> 
            Toast.makeText(this, "Tính năng bộ lọc đang được cập nhật", Toast.LENGTH_SHORT).show());
    }

    private void setupRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tripadvisor-com1.p.rapidapi.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(TravelApiService.class);
    }

    private void searchGeoId(String query) {
        apiService.getAutoComplete(API_KEY, API_HOST, query).enqueue(new Callback<TravelModels.AutoCompleteResponse>() {
            @Override
            public void onResponse(@NonNull Call<TravelModels.AutoCompleteResponse> call, @NonNull Response<TravelModels.AutoCompleteResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    for (TravelModels.TypeaheadResult result : response.body().data) {
                        if (result.geoId != null) {
                            fetchTours(result.geoId);
                            return;
                        }
                    }
                    Toast.makeText(TourActivity.this, "Không tìm thấy mã vùng cho: " + query, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<TravelModels.AutoCompleteResponse> call, @NonNull Throwable t) {
                Toast.makeText(TourActivity.this, "Lỗi tìm địa điểm: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTours(String geoId) {
        apiService.searchAttractions(API_KEY, API_HOST, geoId, "miles", "asc").enqueue(new Callback<TravelModels.AttractionSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<TravelModels.AttractionSearchResponse> call, @NonNull Response<TravelModels.AttractionSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    attractionList.clear();
                    if (response.body().data.attractions != null) {
                        attractionList.addAll(response.body().data.attractions);
                    }
                    adapter.notifyDataSetChanged();
                    
                    if (attractionList.isEmpty()) {
                        Toast.makeText(TourActivity.this, "Không tìm thấy tour nào tại đây", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<TravelModels.AttractionSearchResponse> call, @NonNull Throwable t) {
                Toast.makeText(TourActivity.this, "Lỗi nạp tour: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
