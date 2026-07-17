package com.example.doan1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HotelActivity extends AppCompatActivity {

    private TextView tvCheckInDate, tvCheckOutDate;
    private EditText etLocation, etRooms, etAdults;
    private TextView tvOvernight, tvDayUse;
    private ApiService apiService;
    private Calendar checkInCalendar, checkOutCalendar;
    
    private final String API_KEY = "3058d9105emsh5289cdf3f4c04f4p1a2dd6jsn4927b6844d1c";
    private final String API_HOST = "apidojo-booking-v1.p.rapidapi.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        // Khởi tạo ngày mặc định (Ngày mai và ngày kia)
        checkInCalendar = Calendar.getInstance();
        checkInCalendar.add(Calendar.DAY_OF_YEAR, 1);
        checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.add(Calendar.DAY_OF_YEAR, 2);

        // Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apidojo-booking-v1.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Khai báo các view
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnSearch = findViewById(R.id.btnSearchHotel);
        LinearLayout checkInContainer = findViewById(R.id.checkInContainer);
        LinearLayout checkOutContainer = findViewById(R.id.checkOutContainer);
        
        tvCheckInDate = findViewById(R.id.tvCheckInDate);
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate);
        etLocation = findViewById(R.id.etLocation);
        etRooms = findViewById(R.id.etRooms);
        etAdults = findViewById(R.id.etAdults);
        tvOvernight = findViewById(R.id.tvOvernight);
        tvDayUse = findViewById(R.id.tvDayUse);

        // Hiển thị ngày mặc định lên UI
        updateDateLabels();

        // Xử lý nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Xử lý chuyển tab
        tvOvernight.setOnClickListener(v -> selectTab(true));
        tvDayUse.setOnClickListener(v -> selectTab(false));

        // Xử lý chọn ngày
        checkInContainer.setOnClickListener(v -> showDatePicker(true));
        checkOutContainer.setOnClickListener(v -> showDatePicker(false));

        // Nút tìm kiếm thật
        btnSearch.setOnClickListener(v -> performSearch());

        // Mở màn hình bản đồ
        ImageButton btnOpenMap = findViewById(R.id.btnOpenMap);
        btnOpenMap.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, MapActivity.class);
            startActivity(intent);
        });
    }

    private void updateDateLabels() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvCheckInDate.setText(displayFormat.format(checkInCalendar.getTime()));
        tvCheckOutDate.setText(displayFormat.format(checkOutCalendar.getTime()));
    }

    private void performSearch() {
        String location = etLocation.getText().toString().trim();
        int adults = 2;
        try { adults = Integer.parseInt(etAdults.getText().toString()); } catch (Exception e) {}
        int rooms = 1;
        try { rooms = Integer.parseInt(etRooms.getText().toString()); } catch (Exception e) {}

        // Định dạng ngày chuẩn yyyy-MM-dd cho API
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String apiCheckIn = apiFormat.format(checkInCalendar.getTime());
        String apiCheckOut = apiFormat.format(checkOutCalendar.getTime());

        Toast.makeText(this, "Đang truy vấn dữ liệu thật từ Booking.com...", Toast.LENGTH_SHORT).show();

        // Sử dụng mã vùng Hà Nội: -3714993 (đảm bảo có kết quả để test)
        apiService.getHotels(API_KEY, API_HOST, "city", "-3714993", apiCheckIn, apiCheckOut, adults, rooms, "metric", "vi_VN")
                .enqueue(new Callback<HotelResponse>() {
                    @Override
                    public void onResponse(Call<HotelResponse> call, Response<HotelResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Hotel> hotels = response.body().getHotels();
                            if (hotels != null && !hotels.isEmpty()) {
                                Hotel h = hotels.get(0);
                                String msg = "THÀNH CÔNG!\n" + h.getName() + "\nGiá: " + String.format(Locale.getDefault(), "%,.0f", h.getPrice()) + " VND";
                                Toast.makeText(HotelActivity.this, msg, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(HotelActivity.this, "Không có phòng cho ngày này (Server trả về rỗng)", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            String error = "Lỗi " + response.code();
                            if (response.code() == 403) error += ": Chưa Subscribe gói Free trên RapidAPI";
                            Toast.makeText(HotelActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<HotelResponse> call, Throwable t) {
                        Toast.makeText(HotelActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void selectTab(boolean overnight) {
        LinearLayout checkOutContainer = findViewById(R.id.checkOutContainer);
        if (overnight) {
            tvOvernight.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            tvDayUse.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            checkOutContainer.setVisibility(android.view.View.VISIBLE);
        } else {
            tvDayUse.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            tvOvernight.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            checkOutContainer.setVisibility(android.view.View.GONE);
        }
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar activeCalendar = isCheckIn ? checkInCalendar : checkOutCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    activeCalendar.set(Calendar.YEAR, year);
                    activeCalendar.set(Calendar.MONTH, month);
                    activeCalendar.set(Calendar.DAY_OF_MONTH, day);
                    updateDateLabels();
                }, activeCalendar.get(Calendar.YEAR), activeCalendar.get(Calendar.MONTH), activeCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
