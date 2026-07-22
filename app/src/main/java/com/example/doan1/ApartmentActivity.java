package com.example.doan1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import androidx.annotation.NonNull;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.location.Address;
import android.location.Geocoder;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApartmentActivity extends AppCompatActivity {

    private TextView tvCheckIn, tvCheckOut;
    private EditText etRooms, etAdults, etLocation;
    private RecyclerView rvRealHotels;
    private AirbnbAdapter airbnbAdapter;
    private List<AirbnbModels.StaySearchResult> apartmentList = new ArrayList<>();
    private List<AirbnbModels.StaySearchResult> fullApartmentList = new ArrayList<>();
    private AirbnbApiService apiService; // Khai báo dùng chung để tăng tốc
    private Calendar checkInCalendar, checkOutCalendar;
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private FusedLocationProviderClient fusedLocationClient;

    private String selectedCategory = null;
    private Integer selectedPets = 0;
    private Integer selectedBedrooms = null;

    private static final String API_KEY = "3058d9105emsh5289cdf3f4c04f4p1a2dd6jsn4927b6844d1c";
    private static final String API_HOST = "airbnb-search.p.rapidapi.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment);

        ImageButton btnBack = findViewById(R.id.btnBackApt);
        btnBack.setOnClickListener(v -> finish());

        tvCheckIn = findViewById(R.id.tvCheckIn);
        tvCheckOut = findViewById(R.id.tvCheckOut);
        etRooms = findViewById(R.id.etRooms);
        etAdults = findViewById(R.id.etAdults);
        etLocation = findViewById(R.id.etLocation);
        Button btnSearch = findViewById(R.id.btnSearch);
        rvRealHotels = findViewById(R.id.rvRealHotels);

        // Setup RecyclerView
        rvRealHotels.setLayoutManager(new LinearLayoutManager(this));
        airbnbAdapter = new AirbnbAdapter(apartmentList, item -> {
            android.content.Intent intent = new android.content.Intent(ApartmentActivity.this, ApartmentDetailActivity.class);
            intent.putExtra("listing", item);
            startActivity(intent);
        });
        rvRealHotels.setAdapter(airbnbAdapter);

        // Khởi tạo API Service dùng chung để tiết kiệm thời gian kết nối
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.NONE); // Tắt log để tăng tốc độ xử lý
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://airbnb-search.p.rapidapi.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(AirbnbApiService.class);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Thiết lập ngày mặc định (Gần hơn để dễ tìm phòng)
        checkInCalendar = Calendar.getInstance();
        checkInCalendar.add(Calendar.DAY_OF_YEAR, 3);
        checkOutCalendar = (Calendar) checkInCalendar.clone();
        checkOutCalendar.add(Calendar.DAY_OF_YEAR, 2);
        updateDateLabels();

        // Nút vị trí hiện tại
        findViewById(R.id.ivCurrentLocation).setOnClickListener(v -> getCurrentLocationAndSearch());

        // Nút chọn số người/phòng
        findViewById(R.id.btnSelectGuests).setOnClickListener(v -> {
            etRooms.requestFocus();
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(etRooms, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        });

        // Nút bản đồ
        findViewById(R.id.btnMap).setOnClickListener(v -> {
            if (apartmentList.isEmpty()) {
                // Nếu chưa tìm kiếm, lấy vị trí hiện tại và mở bản đồ
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                    return;
                }
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    android.content.Intent intent = new android.content.Intent(this, MapActivity.class);
                    if (location != null) {
                        intent.putExtra("INIT_LAT", location.getLatitude());
                        intent.putExtra("INIT_LNG", location.getLongitude());
                    }
                    startActivity(intent);
                });
                return;
            }
            
            ArrayList<Hotel> hotelsForMap = new ArrayList<>();
            for (AirbnbModels.StaySearchResult item : apartmentList) {
                if (item != null && item.demandStayListing != null && item.demandStayListing.location != null && item.demandStayListing.location.coordinate != null) {
                    Hotel h = new Hotel();
                    h.setName(item.title != null ? item.title : "Căn hộ");
                    h.setAddress(item.demandStayListing.location.localizedCityName);
                    h.setImageUrl(item.contextualPictures != null && !item.contextualPictures.isEmpty() ? item.contextualPictures.get(0).picture : "");
                    
                    try {
                        if (item.avgRatingLocalized != null) {
                            String r = item.avgRatingLocalized.split(" ")[0];
                            h.setRating(Double.parseDouble(r));
                        }
                    } catch (Exception e) { h.setRating(0.0); }

                    h.setLatitude(item.demandStayListing.location.coordinate.latitude);
                    h.setLongitude(item.demandStayListing.location.coordinate.longitude);
                    
                    if (item.listing != null && item.listing.id != null) {
                        h.setAirbnbUrl("https://www.airbnb.com/rooms/" + item.listing.id);
                    }
                    
                    // Logic trích xuất giá tiền mạnh mẽ hơn
                    double priceVal = 0;
                    String priceStr = null;

                    if (item.structuredDisplayPrice != null && item.structuredDisplayPrice.primaryLine != null) {
                        priceStr = item.structuredDisplayPrice.primaryLine.price;
                    } else if (item.pricingQuote != null && item.pricingQuote.structuredStayDisplayPrice != null && 
                             item.pricingQuote.structuredStayDisplayPrice.primaryLine != null) {
                        priceStr = item.pricingQuote.structuredStayDisplayPrice.primaryLine.price;
                    }

                    if (priceStr != null) {
                        try {
                            String pStr = priceStr.replaceAll("[^0-9]", "");
                            if (!pStr.isEmpty()) {
                                priceVal = Double.parseDouble(pStr);
                                if (priceVal < 10000) priceVal *= 25400; 
                            }
                        } catch (Exception e) { priceVal = 0; }
                    }

                    // Chỉ hiện lên bản đồ nếu có giá thật
                    if (priceVal > 0) {
                        h.setPrice(priceVal);
                        hotelsForMap.add(h);
                    }
                }
            }
            
            if (hotelsForMap.isEmpty()) {
                Toast.makeText(this, "Dữ liệu bản đồ không khả dụng", Toast.LENGTH_SHORT).show();
                return;
            }
            
            android.content.Intent intent = new android.content.Intent(this, MapActivity.class);
            intent.putExtra("HOTEL_LIST", hotelsForMap);
            startActivity(intent);
        });

        findViewById(R.id.btnSelectDates).setOnClickListener(v -> showDatePicker(true));
        findViewById(R.id.tvCheckIn).setOnClickListener(v -> showDatePicker(true));
        findViewById(R.id.tvCheckOut).setOnClickListener(v -> showDatePicker(false));
        
        btnSearch.setOnClickListener(v -> {
            String location = etLocation.getText().toString().trim();
            if (location.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa điểm", Toast.LENGTH_SHORT).show();
                return;
            }
            searchAirbnb(location);
        });

        setupFilters();
    }

    private void getCurrentLocationAndSearch() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                try {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        String cityName = addresses.get(0).getLocality();
                        if (cityName == null) cityName = addresses.get(0).getAdminArea();
                        
                        if (cityName != null) {
                            etLocation.setText(cityName);
                            searchAirbnb(cityName);
                        } else {
                            searchAirbnb("Ha Noi"); // Fallback
                        }
                    }
                } catch (Exception e) {
                    searchAirbnb("Ha Noi");
                }
            } else {
                searchAirbnb("Ha Noi");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocationAndSearch();
        } else if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            findViewById(R.id.btnMap).performClick();
        } else {
            searchAirbnb("Ha Noi");
        }
    }

    private void searchAirbnb(String location) {
        // 1. Kiểm tra bộ nhớ đệm (Cache) - Cực nhanh
        List<AirbnbModels.StaySearchResult> cachedData = SearchCacheManager.getCachedResults(this, location);
        if (cachedData != null && !cachedData.isEmpty()) {
            fullApartmentList.clear();
            fullApartmentList.addAll(cachedData);
            
            apartmentList.clear();
            apartmentList.addAll(cachedData);
            airbnbAdapter.notifyDataSetChanged();
            
            // Nếu đang có bộ lọc thì áp dụng luôn
            if (selectedBedrooms != null || selectedCategory != null || selectedPets > 0) {
                applyLocalFilters();
            }
            
            Toast.makeText(this, "Hiển thị tức thì từ bộ nhớ tạm", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Đang truy vấn dữ liệu mới...", Toast.LENGTH_SHORT).show();

        // Bước 1: Lấy placeId từ tên địa điểm (Dùng apiService đã khởi tạo sẵn)
        apiService.autoComplete(API_KEY, API_HOST, location).enqueue(new Callback<AirbnbModels.AutoCompleteResponse>() {
            @Override
            public void onResponse(Call<AirbnbModels.AutoCompleteResponse> call, Response<AirbnbModels.AutoCompleteResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null && !response.body().data.isEmpty()) {
                    AirbnbModels.Suggestion suggestion = response.body().data.get(0);
                    
                    String rawId = suggestion.id;
                    String displayName = suggestion.displayName;
                    String jsonWrapper = "{\"p\":\"" + rawId + "\",\"q\":\"" + displayName + "\"}";
                    
                    String encodedPlaceId = "";
                    try {
                        encodedPlaceId = android.util.Base64.encodeToString(jsonWrapper.getBytes("UTF-8"), android.util.Base64.NO_WRAP);
                    } catch (Exception e) {
                        encodedPlaceId = rawId;
                    }
                    
                    executeActualSearch(encodedPlaceId, location);
                } else {
                    showErrorDialog("Lỗi", "Không tìm thấy địa điểm. Hãy thử nhập tên khác.");
                }
            }

            @Override
            public void onFailure(Call<AirbnbModels.AutoCompleteResponse> call, Throwable t) {
                showErrorDialog("Lỗi kết nối", t.getMessage());
            }
        });
    }

    private void executeActualSearch(String placeId, String originalLocation) {
        String checkin = apiDateFormat.format(checkInCalendar.getTime());
        String checkout = apiDateFormat.format(checkOutCalendar.getTime());
        
        Integer adults = 1;
        Integer roomsInput = null;
        try { 
            String adultsText = etAdults.getText().toString().trim();
            if (!adultsText.isEmpty()) adults = Integer.parseInt(adultsText);
            
            String roomsText = etRooms.getText().toString().trim();
            if (!roomsText.isEmpty()) roomsInput = Integer.parseInt(roomsText);
        } catch (Exception e) {}
        
        Integer finalBedrooms = (selectedBedrooms != null && selectedBedrooms > 1) ? selectedBedrooms : roomsInput;
        if (finalBedrooms != null && finalBedrooms <= 1) finalBedrooms = null;
        
        Integer pets = (selectedPets > 0) ? 1 : null;

        apiService.searchByLocation(API_KEY, API_HOST, placeId, checkin, checkout, adults, null, null, pets, finalBedrooms, selectedCategory, "VND", "vi-VN")
                .enqueue(new Callback<AirbnbModels.AirbnbResponse>() {
            @Override
            public void onResponse(Call<AirbnbModels.AirbnbResponse> call, Response<AirbnbModels.AirbnbResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AirbnbModels.AirbnbResponse body = response.body();
                    
                    if (body.status && body.data != null && body.data.searchResults != null && !body.data.searchResults.isEmpty()) {
                        fullApartmentList.clear();
                        fullApartmentList.addAll(body.data.searchResults);
                        
                        apartmentList.clear();
                        apartmentList.addAll(body.data.searchResults);
                        airbnbAdapter.notifyDataSetChanged();
                        
                        // Áp dụng bộ lọc nếu có
                        if (selectedBedrooms != null || selectedCategory != null || selectedPets > 0) {
                            applyLocalFilters();
                        }
                        
                        // Lưu vào bộ nhớ đệm để lần sau hiện ngay lập tức
                        SearchCacheManager.saveCache(ApartmentActivity.this, originalLocation, body.data.searchResults);
                    } else {
                        String apiMsg = body.message != null ? body.message : "Không tìm thấy phòng trống.";
                        showErrorDialog("Thông báo", apiMsg);
                    }
                } else {
                    showErrorDialog("Lỗi", "Mã lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AirbnbModels.AirbnbResponse> call, Throwable t) {
                showErrorDialog("Lỗi kết nối", t.getMessage());
            }
        });
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar calendar = isCheckIn ? checkInCalendar : checkOutCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            calendar.set(y, m, d);
            if (isCheckIn) {
                // Đảm bảo ngày checkout sau ngày checkin ít nhất 1 ngày
                if (checkOutCalendar.before(checkInCalendar) || checkOutCalendar.equals(checkInCalendar)) {
                    checkOutCalendar = (Calendar) checkInCalendar.clone();
                    checkOutCalendar.add(Calendar.DAY_OF_YEAR, 1);
                }
            } else {
                // Nếu người dùng chọn ngày checkout trước checkin, đặt lại checkin
                if (checkOutCalendar.before(checkInCalendar)) {
                    Toast.makeText(this, "Ngày trả phòng phải sau ngày nhận phòng", Toast.LENGTH_SHORT).show();
                    checkOutCalendar = (Calendar) checkInCalendar.clone();
                    checkOutCalendar.add(Calendar.DAY_OF_YEAR, 1);
                }
            }
            updateDateLabels();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Không cho phép chọn ngày trong quá khứ
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        
        datePickerDialog.show();
    }

    private void updateDateLabels() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvCheckIn.setText(df.format(checkInCalendar.getTime()));
        tvCheckOut.setText(df.format(checkOutCalendar.getTime()));
    }

    private void showErrorDialog(String title, String msg) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title).setMessage(msg).setPositiveButton("Đóng", null).show();
    }

    private void setupFilters() {
        TextView btnClearFilters = findViewById(R.id.btnClearFilters);
        btnClearFilters.setOnClickListener(v -> {
            selectedCategory = null;
            selectedBedrooms = null;
            selectedPets = 0;
            
            apartmentList.clear();
            apartmentList.addAll(fullApartmentList);
            airbnbAdapter.notifyDataSetChanged();
            
            btnClearFilters.setVisibility(android.view.View.GONE);
            Toast.makeText(this, "Đã xóa toàn bộ bộ lọc", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.filterPropertyType).setOnClickListener(v -> showFilterBottomSheet("Loại chỗ ở", 
            new ArrayList<>(Arrays.asList("Biệt thự", "Căn hộ", "Bungalow", "Nhà dân"))));
        findViewById(R.id.filterUniqueStay).setOnClickListener(v -> showFilterBottomSheet("Nơi ở độc đáo", 
            new ArrayList<>(Arrays.asList("Thuyền", "Nông trại", "Lều", "Cabin"))));
        findViewById(R.id.filterRooms).setOnClickListener(v -> showFilterBottomSheet("Số phòng ngủ", 
            new ArrayList<>(Arrays.asList("1 phòng ngủ", "2 phòng ngủ", "3 phòng ngủ"))));
        findViewById(R.id.filterRules).setOnClickListener(v -> showFilterBottomSheet("Quy định", 
            new ArrayList<>(Arrays.asList("Cho phép thú cưng"))));
    }

    private void showFilterBottomSheet(String title, ArrayList<String> items) {
        FilterBottomSheetDialogFragment sheet = FilterBottomSheetDialogFragment.newInstance(title, items);
        sheet.setOnFilterAppliedListener((filterTitle, selectedItems) -> {
            if (selectedItems.isEmpty()) return;
            String first = selectedItems.get(0);
            if (filterTitle.equals("Nơi ở độc đáo")) {
                if (first.equals("Thuyền")) selectedCategory = "boats";
                else if (first.equals("Nông trại")) selectedCategory = "farms";
                else if (first.equals("Lều")) selectedCategory = "camping";
                else if (first.equals("Cabin")) selectedCategory = "cabins";
                else selectedCategory = null;
            } else if (filterTitle.equals("Loại chỗ ở")) {
                if (first.equals("Biệt thự")) selectedCategory = "villas";
                else if (first.equals("Căn hộ")) selectedCategory = "apartments";
                else if (first.equals("Bungalow")) selectedCategory = "bungalows";
                else selectedCategory = null;
            } else if (filterTitle.equals("Số phòng ngủ")) {
                try {
                    selectedBedrooms = Integer.parseInt(first.split(" ")[0]);
                    etRooms.setText(String.valueOf(selectedBedrooms)); // Đồng bộ lên thanh tìm kiếm
                } catch (Exception e) {
                    selectedBedrooms = null;
                }
            } else if (filterTitle.equals("Quy định")) {
                if (first.equals("Cho phép thú cưng")) selectedPets = 1;
                else selectedPets = 0;
            }

            // Lọc cục bộ ngay lập tức từ bộ nhớ đệm
            if (!fullApartmentList.isEmpty()) {
                applyLocalFilters();
            } else {
                String location = etLocation.getText().toString().trim();
                if (location.isEmpty()) location = "Ha Noi";
                searchAirbnb(location);
            }
        });
        sheet.show(getSupportFragmentManager(), "FilterSheet");
    }

    private void applyLocalFilters() {
        if (fullApartmentList.isEmpty()) return;

        List<AirbnbModels.StaySearchResult> filteredList = new ArrayList<>();
        for (AirbnbModels.StaySearchResult item : fullApartmentList) {
            boolean matches = true;

            // 1. Lọc theo Số phòng ngủ (Bedroom) - Cải tiến quét từ khóa
            if (selectedBedrooms != null) {
                boolean bedroomMatch = false;
                String title = item.title != null ? item.title.toLowerCase() : "";
                String subtitle = item.subtitle != null ? item.subtitle.toLowerCase() : "";
                String target = String.valueOf(selectedBedrooms);

                // Kiểm tra trong structuredContent (Ưu tiên)
                if (item.structuredContent != null && item.structuredContent.primaryLine != null) {
                    for (AirbnbModels.MainSectionMessage msg : item.structuredContent.primaryLine) {
                        if (msg != null && msg.type != null && msg.type.contains("BEDROOM") && msg.body != null) {
                            if (msg.body.contains(target)) {
                                bedroomMatch = true;
                                break;
                            }
                        }
                    }
                }

                // Nếu chưa tìm thấy, quét trong title và subtitle
                if (!bedroomMatch) {
                    String fullInfo = title + " " + subtitle;
                    if (selectedBedrooms == 1 && (fullInfo.contains("studio") || fullInfo.contains("1 phòng") || fullInfo.contains("1 br") || fullInfo.contains("1 bedroom"))) {
                        bedroomMatch = true;
                    } else if (fullInfo.contains(target + " phòng") || fullInfo.contains(target + " br") || fullInfo.contains(target + " bedroom")) {
                        bedroomMatch = true;
                    }
                }
                
                if (!bedroomMatch) matches = false;
            }

            // 2. Lọc theo Loại chỗ ở / Nơi ở độc đáo (Category)
            if (matches && selectedCategory != null) {
                String key = selectedCategory.toLowerCase();
                String title = item.title != null ? item.title.toLowerCase() : "";
                String subtitle = item.subtitle != null ? item.subtitle.toLowerCase() : "";
                String fullInfo = title + " " + subtitle;
                
                boolean categoryMatch = false;
                if (key.equals("villas") && (fullInfo.contains("biệt thự") || fullInfo.contains("villa"))) categoryMatch = true;
                else if (key.equals("apartments") && (fullInfo.contains("căn hộ") || fullInfo.contains("apartment") || fullInfo.contains("condo"))) categoryMatch = true;
                else if (key.equals("bungalows") && fullInfo.contains("bungalow")) categoryMatch = true;
                else if (key.equals("boats") && (fullInfo.contains("thuyền") || fullInfo.contains("boat") || fullInfo.contains("houseboat"))) categoryMatch = true;
                else if (key.equals("farms") && (fullInfo.contains("farm") || fullInfo.contains("trang trại") || fullInfo.contains("nông trại"))) categoryMatch = true;
                else if (key.equals("camping") && (fullInfo.contains("lều") || fullInfo.contains("camp") || fullInfo.contains("tent") || fullInfo.contains("glamping"))) categoryMatch = true;
                else if (key.equals("cabins") && fullInfo.contains("cabin")) categoryMatch = true;
                
                if (!categoryMatch) matches = false;
            }

            // 3. Lọc theo Thú cưng - Quét từ khóa thông minh
            if (matches && selectedPets != null && selectedPets > 0) {
                String title = item.title != null ? item.title.toLowerCase() : "";
                String subtitle = item.subtitle != null ? item.subtitle.toLowerCase() : "";
                String fullInfo = title + " " + subtitle;
                
                boolean petMatch = false;
                // Nếu chứa từ khóa cho phép
                if (fullInfo.contains("pet") || fullInfo.contains("thú cưng") || fullInfo.contains("động vật") || fullInfo.contains("allow")) {
                    // Trừ trường hợp chứa từ phủ định
                    if (!fullInfo.contains("no pet") && !fullInfo.contains("không cho phép") && !fullInfo.contains("not allow")) {
                        petMatch = true;
                    }
                }
                
                // Mặc định nhiều căn hộ cho phép nhưng không ghi, nên nếu lọc PET ta chấp nhận rủi ro quét từ khóa
                if (!petMatch) matches = false;
            }

            if (matches) filteredList.add(item);
        }

        apartmentList.clear();
        apartmentList.addAll(filteredList);
        airbnbAdapter.notifyDataSetChanged();
        
        // Hiện/ẩn nút xóa lọc
        findViewById(R.id.btnClearFilters).setVisibility(android.view.View.VISIBLE);
        
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không có căn hộ nào khớp với bộ lọc", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Đã lọc: " + filteredList.size() + " kết quả", Toast.LENGTH_SHORT).show();
        }
    }
}
