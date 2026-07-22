package com.example.doan1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarRentalActivity extends AppCompatActivity {

    private TextView tabRentCar, tabAirportTransfer, tvHeaderTitle;
    private TextView subTabPickUp, subTabDropOff;
    private TextView tvDatesCar, tvAgeCar;
    private EditText etLocationCar;
    private View containerAirportTransfer, containerRentCar;
    private View sectionPickUp, sectionDropOff;
    
    private RecyclerView rvCars;
    private CarRentalAdapter adapter;
    private List<CarRentalModels.CarRental> carList = new ArrayList<>();
    private CarRentalApiService apiService;

    private Calendar pickUpCalendar, dropOffCalendar;
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    
    private static final String API_KEY = "d0dafa9f25msh35fc9ba30aff965p1e8538jsna97ae9bf3eb4";
    private static final String API_HOST = "booking-com18.p.rapidapi.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_rental);

        initViews();
        setupRetrofit();
        setupEvents();

        // Default setup
        pickUpCalendar = Calendar.getInstance();
        pickUpCalendar.add(Calendar.DAY_OF_YEAR, 7); // Mặc định thuê sau 1 tuần để chắc chắn có xe
        dropOffCalendar = (Calendar) pickUpCalendar.clone();
        dropOffCalendar.add(Calendar.DAY_OF_YEAR, 3);
        updateDateLabels();

        selectMainTab(true);
    }

    private void initViews() {
        tabRentCar = findViewById(R.id.tabRentCar);
        tabAirportTransfer = findViewById(R.id.tabAirportTransfer);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        containerAirportTransfer = findViewById(R.id.containerAirportTransfer);
        containerRentCar = findViewById(R.id.containerRentCar);
        
        subTabPickUp = findViewById(R.id.subTabPickUp);
        subTabDropOff = findViewById(R.id.subTabDropOff);
        sectionPickUp = findViewById(R.id.sectionPickUp);
        sectionDropOff = findViewById(R.id.sectionDropOff);

        etLocationCar = findViewById(R.id.etLocationCar);
        tvDatesCar = findViewById(R.id.tvDatesCar);
        tvAgeCar = findViewById(R.id.tvAgeCar);
        
        rvCars = findViewById(R.id.rvCars);
        rvCars.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CarRentalAdapter(carList);
        rvCars.setAdapter(adapter);
    }

    private void setupRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://booking-com18.p.rapidapi.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(CarRentalApiService.class);
    }

    private void setupEvents() {
        findViewById(R.id.btnBackCar).setOnClickListener(v -> finish());

        tabRentCar.setOnClickListener(v -> selectMainTab(true));
        tabAirportTransfer.setOnClickListener(v -> selectMainTab(false));
        
        subTabPickUp.setOnClickListener(v -> selectSubTab(true));
        subTabDropOff.setOnClickListener(v -> selectSubTab(false));

        findViewById(R.id.btnSelectDatesCar).setOnClickListener(v -> showDatePickerCar());
        findViewById(R.id.btnSelectAgeCar).setOnClickListener(v -> showAgeSelectionDialog());

        findViewById(R.id.btnSearchCar).setOnClickListener(v -> {
            String query = etLocationCar.getText().toString().trim();
            if (query.isEmpty()) query = "Hanoi";
            searchLocationAndCars(query);
        });
    }

    private void searchLocationAndCars(String query) {
        Toast.makeText(this, "Đang tìm mã vùng cho: " + query, Toast.LENGTH_SHORT).show();
        apiService.autoComplete(API_KEY, API_HOST, query).enqueue(new Callback<CarRentalModels.AutoCompleteResponse>() {
            @Override
            public void onResponse(@NonNull Call<CarRentalModels.AutoCompleteResponse> call, @NonNull Response<CarRentalModels.AutoCompleteResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null && !response.body().data.isEmpty()) {
                    // Ưu tiên lấy kết quả đầu tiên có ID hợp lệ
                    String pickUpId = response.body().data.get(0).id;
                    Log.d("DEBUG_API", "Tìm thấy pickUpId: " + pickUpId);
                    fetchCars(pickUpId);
                } else {
                    Toast.makeText(CarRentalActivity.this, "Không tìm thấy mã vùng cho: " + query, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CarRentalModels.AutoCompleteResponse> call, @NonNull Throwable t) {
                Toast.makeText(CarRentalActivity.this, "Lỗi kết nối địa điểm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCars(String pickUpId) {
        String pDate = apiDateFormat.format(pickUpCalendar.getTime());
        String dDate = apiDateFormat.format(dropOffCalendar.getTime());

        // Gửi cả pickUpId và dropOffId (thường giống nhau nếu thuê khứ hồi)
        apiService.searchCars(API_KEY, API_HOST, pickUpId, pickUpId, pDate, "10:00", dDate, "10:00", "VND")
                .enqueue(new Callback<CarRentalModels.CarSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<CarRentalModels.CarSearchResponse> call, @NonNull Response<CarRentalModels.CarSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    carList.clear();
                    if (response.body().data.carRentals != null) {
                        carList.addAll(response.body().data.carRentals);
                    }
                    adapter.notifyDataSetChanged();
                    if (carList.isEmpty()) {
                        Toast.makeText(CarRentalActivity.this, "Không có xe nào khả dụng tại thời điểm này", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Mã: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += "\nChi tiết: " + response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    Log.e("DEBUG_API", "LOI_API_XE: " + errorMsg);
                    Toast.makeText(CarRentalActivity.this, "Lỗi API xe: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CarRentalModels.CarSearchResponse> call, @NonNull Throwable t) {
                Log.e("DEBUG_API", "LOI_KET_NOI_XE: " + t.getMessage(), t);
                Toast.makeText(CarRentalActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDatePickerCar() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            pickUpCalendar.set(year, month, dayOfMonth);
            dropOffCalendar = (Calendar) pickUpCalendar.clone();
            dropOffCalendar.add(Calendar.DAY_OF_YEAR, 3);
            updateDateLabels();
        }, pickUpCalendar.get(Calendar.YEAR), pickUpCalendar.get(Calendar.MONTH), pickUpCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateLabels() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDatesCar.setText("Lấy: " + displayFormat.format(pickUpCalendar.getTime()) + " - Trả: " + displayFormat.format(dropOffCalendar.getTime()));
    }

    private void showAgeSelectionDialog() {
        String[] ages = {"18-25", "26-30", "30-60", "Trên 60"};
        new AlertDialog.Builder(this)
            .setTitle("Chọn độ tuổi tài xế")
            .setItems(ages, (dialog, which) -> {
                tvAgeCar.setText("Độ tuổi tài xế " + ages[which]);
            })
            .show();
    }

    private void selectMainTab(boolean isRentCar) {
        if (isRentCar) {
            tabRentCar.setBackgroundColor(Color.WHITE);
            tabRentCar.setTextColor(Color.parseColor("#333333"));
            tabAirportTransfer.setBackgroundColor(Color.parseColor("#F2F3F7"));
            tabAirportTransfer.setTextColor(Color.parseColor("#888888"));
            tvHeaderTitle.setText("Thuê xe");
            containerRentCar.setVisibility(View.VISIBLE);
            containerAirportTransfer.setVisibility(View.GONE);
        } else {
            tabAirportTransfer.setBackgroundColor(Color.WHITE);
            tabAirportTransfer.setTextColor(Color.parseColor("#333333"));
            tabRentCar.setBackgroundColor(Color.parseColor("#F2F3F7"));
            tabRentCar.setTextColor(Color.parseColor("#888888"));
            tvHeaderTitle.setText("Đưa Đón Sân Bay");
            containerRentCar.setVisibility(View.GONE);
            containerAirportTransfer.setVisibility(View.VISIBLE);
        }
    }

    private void selectSubTab(boolean isPickUp) {
        if (isPickUp) {
            subTabPickUp.setBackgroundColor(Color.WHITE);
            subTabPickUp.setTextColor(Color.parseColor("#333333"));
            subTabDropOff.setBackgroundColor(Color.TRANSPARENT);
            subTabDropOff.setTextColor(Color.parseColor("#888888"));
            sectionPickUp.setVisibility(View.VISIBLE);
            sectionDropOff.setVisibility(View.GONE);
        } else {
            subTabDropOff.setBackgroundColor(Color.WHITE);
            subTabDropOff.setTextColor(Color.parseColor("#333333"));
            subTabPickUp.setBackgroundColor(Color.TRANSPARENT);
            subTabPickUp.setTextColor(Color.parseColor("#888888"));
            sectionPickUp.setVisibility(View.GONE);
            sectionDropOff.setVisibility(View.VISIBLE);
        }
    }
}
