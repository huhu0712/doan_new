package com.example.doan1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.io.Serializable;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
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

public class HotelActivity extends AppCompatActivity {

    private TextView tvCheckInDate, tvCheckOutDate;
    private EditText etLocation, etRooms, etAdults;
    private BookingApiService apiService;
    private Calendar checkInCalendar, checkOutCalendar;
    private List<Hotel> lastSearchResult = new ArrayList<>();
    
    // API Key mới từ hình ảnh của bạn
    private final String API_KEY = "3058d9105emsh5289cdf3f4c04f4p1a2dd6jsn4927b6844d1c";
    private final String API_HOST = "booking-com15.p.rapidapi.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://booking-com15.p.rapidapi.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(BookingApiService.class);

        // Thiết lập ngày: 1 tháng sau (Chắc chắn có phòng)
        checkInCalendar = Calendar.getInstance();
        checkInCalendar.add(Calendar.MONTH, 1);
        checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.add(Calendar.MONTH, 1);
        checkOutCalendar.add(Calendar.DAY_OF_YEAR, 2);

        tvCheckInDate = findViewById(R.id.tvCheckInDate);
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate);
        etLocation = findViewById(R.id.etLocation);
        etRooms = findViewById(R.id.etRooms);
        etAdults = findViewById(R.id.etAdults);

        updateDateLabels();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.checkInContainer).setOnClickListener(v -> showDatePicker(true));
        findViewById(R.id.checkOutContainer).setOnClickListener(v -> showDatePicker(false));
        findViewById(R.id.btnSearchHotel).setOnClickListener(v -> performApiSearch());

        findViewById(R.id.btnOpenMap).setOnClickListener(v -> {
            if (lastSearchResult == null || lastSearchResult.isEmpty()) {
                Toast.makeText(this, "Hãy nhấn Tìm kiếm để 'mượn' dữ liệu trước!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("HOTEL_LIST", (Serializable) lastSearchResult);
                startActivity(intent);
            }
        });
    }

    private void performApiSearch() {
        String locationName = etLocation.getText().toString().trim();
        if (locationName.isEmpty()) locationName = "Ha Noi";

        Toast.makeText(this, "Đang tìm mã vùng cho: " + locationName, Toast.LENGTH_SHORT).show();
        
        // Bước 1: Tìm dest_id từ tên địa điểm
        apiService.searchDestination(API_KEY, API_HOST, locationName, "vi_VN")
                .enqueue(new Callback<BookingModels.LocationResponse>() {
                    @Override
                    public void onResponse(Call<BookingModels.LocationResponse> call, Response<BookingModels.LocationResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().data != null && !response.body().data.isEmpty()) {
                            JsonElement firstItem = response.body().data.get(0);
                            String destId = "";
                            
                            if (firstItem.isJsonObject()) {
                                // Nếu là Object thì lấy dest_id
                                BookingModels.LocationItem item = new Gson().fromJson(firstItem, BookingModels.LocationItem.class);
                                destId = item.destId;
                            } else if (firstItem.isJsonPrimitive()) {
                                // Nếu là String thì dùng luôn (nhưng destId thường là số/id)
                                destId = firstItem.getAsString();
                            }
                            
                            if (destId != null && !destId.isEmpty()) {
                                fetchHotelsByDestId(destId);
                            } else {
                                showErrorDialog("Lỗi Dữ Liệu", "Không tìm thấy mã vùng (dest_id) hợp lệ.");
                                loadFallbackData();
                            }
                        } else {
                            Log.e("HotelActivity", "Search error: " + response.code() + " - " + response.message());
                            Toast.makeText(HotelActivity.this, "Không tìm thấy địa điểm hoặc lỗi API!", Toast.LENGTH_SHORT).show();
                            loadFallbackData();
                        }
                    }

                    @Override
                    public void onFailure(Call<BookingModels.LocationResponse> call, Throwable t) {
                        Log.e("DEBUG_API", "LOI_KET_NOI_TIM_DIA_DANH: " + t.getMessage(), t);
                        loadFallbackData();
                        showErrorDialog("Lỗi Kết Nối Địa Danh", 
                            "Không thể kết nối đến máy chủ.\n\n" +
                            "Chi tiết: " + t.toString() + "\n\n" +
                            "Vui lòng kiểm tra Internet hoặc VPN của bạn.");
                    }
                });
    }

    private void fetchHotelsByDestId(String destId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String arrival = sdf.format(checkInCalendar.getTime());
        String departure = sdf.format(checkOutCalendar.getTime());

        Toast.makeText(this, "Đang tìm phòng với bộ lọc...", Toast.LENGTH_SHORT).show();

        // Thêm các tham số bộ lọc (ví dụ: sắp xếp theo mức độ phổ biến, đơn vị VND)
        apiService.getHotels(API_KEY, API_HOST, destId, arrival, departure, 2, 1, "metric", "VND", "vi_VN", 
                "popularity", null, null, null)
                .enqueue(new Callback<BookingModels.BookingResponse>() {
                    @Override
                    public void onResponse(Call<BookingModels.BookingResponse> call, Response<BookingModels.BookingResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<BookingModels.HotelItem> items = response.body().result;
                            if (items != null && !items.isEmpty()) {
                                lastSearchResult.clear();
                                for (BookingModels.HotelItem item : items) {
                                    if ("property_card".equals(item.type)) {
                                        Hotel h = new Hotel();
                                        h.setName(item.hotelName);
                                        if (item.priceBreakdown != null && item.priceBreakdown.grossAmount != null) {
                                            h.setPrice(item.priceBreakdown.grossAmount.value);
                                        }
                                        h.setImageUrl(item.mainPhotoUrl);
                                        h.setRating(item.reviewScore);
                                        h.setAddress(item.address);
                                        h.setLatitude(item.latitude);
                                        h.setLongitude(item.longitude);
                                        lastSearchResult.add(h);
                                    }
                                }
                                Toast.makeText(HotelActivity.this, "Tìm thấy " + lastSearchResult.size() + " phòng.", Toast.LENGTH_LONG).show();
                            } else {
                                loadFallbackData();
                            }
                        } else {
                            loadFallbackData();
                        }
                    }

                    @Override
                    public void onFailure(Call<BookingModels.BookingResponse> call, Throwable t) {
                        Log.e("DEBUG_API", "LOI_KET_NOI_TIM_KHACH_SAN: " + t.getMessage(), t);
                        loadFallbackData();
                        showErrorDialog("Lỗi Kết Nối Khách Sạn", 
                            "Không thể tìm danh sách phòng.\n\n" +
                            "Chi tiết: " + t.toString());
                    }
                });
    }

    private void loadFallbackData() {
        lastSearchResult.clear();
        String[] hNames = {"Metropole Hanoi", "Lotte Hotel", "Melia Hanoi"};
        double[] lats = {21.0252, 21.0319, 21.0245}, lngs = {105.8572, 105.8123, 105.8492};
        for(int i=0; i<3; i++) {
            Hotel h = new Hotel(); h.setName(hNames[i]); h.setPrice(4000000);
            h.setLatitude(lats[i]); h.setLongitude(lngs[i]); h.setAddress("Hà Nội"); h.setRating(9.5);
            lastSearchResult.add(h);
        }
    }

    private void updateDateLabels() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvCheckInDate.setText(displayFormat.format(checkInCalendar.getTime()));
        tvCheckOutDate.setText(displayFormat.format(checkOutCalendar.getTime()));
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar cal = isCheckIn ? checkInCalendar : checkOutCalendar;
        new DatePickerDialog(this, (v, y, m, d) -> {
            cal.set(y, m, d); updateDateLabels();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showErrorDialog(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Đã hiểu", null)
                .show();
    }
}
