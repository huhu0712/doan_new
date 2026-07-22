package com.example.doan1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import android.content.Intent;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {
    private MapView map;
    private RecyclerView rvHotels;
    private HotelMapAdapter adapter;
    private List<Hotel> hotelList = new ArrayList<>();
    private int selectedIndex = 0;

    // TODO: Thay thế bằng API Key MapTiler của bạn
    private static final String MAPTILER_KEY = "2kaUl5OF1QGLqFs9H1WI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        map = findViewById(R.id.mapView);
        
        // Cấu hình MapTiler Tile Source (Dùng Streets-v2 ổn định nhất)
        XYTileSource mapTilerSource = new XYTileSource(
                "MapTiler", 1, 20, 256, ".png?key=" + MAPTILER_KEY,
                new String[] { "https://api.maptiler.com/maps/streets-v2/256/" }
        );
        map.setTileSource(mapTilerSource);
        map.setTilesScaledToDpi(true); // Sửa lỗi mờ, giúp bản đồ nét căng
        map.setMultiTouchControls(true);

        rvHotels = findViewById(R.id.rvHotelsMap);

        // NHẬN DỮ LIỆU THẬT VÀ KIỂM TRA LỖI TRỐNG
        double initLat = getIntent().getDoubleExtra("INIT_LAT", 0);
        double initLng = getIntent().getDoubleExtra("INIT_LNG", 0);

        try {
            List<Hotel> receivedHotels = (List<Hotel>) getIntent().getSerializableExtra("HOTEL_LIST");
            if (receivedHotels != null && !receivedHotels.isEmpty()) {
                for (Hotel h : receivedHotels) {
                    if (h.getLatitude() != 0 && h.getLongitude() != 0) {
                        hotelList.add(h);
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi nạp dữ liệu bản đồ", Toast.LENGTH_SHORT).show();
        }

        setupRecyclerView();
        displayHotels();

        if (!hotelList.isEmpty()) {
            moveToHotel(hotelList.get(0));
        } else if (initLat != 0 && initLng != 0) {
            GeoPoint startPoint = new GeoPoint(initLat, initLng);
            map.getController().setCenter(startPoint);
            map.getController().setZoom(15.0);
            
            Marker startMarker = new Marker(map);
            startMarker.setPosition(startPoint);
            startMarker.setTitle("Vị trí của bạn");
            startMarker.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_mylocation));
            map.getOverlays().add(startMarker);
        }

        findViewById(R.id.btnBackMap).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvHotels.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new HotelMapAdapter(hotelList, hotel -> {
            if (hotel.getAirbnbUrl() != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(hotel.getAirbnbUrl()));
                startActivity(intent);
            }
        });
        rvHotels.setAdapter(adapter);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvHotels);

        rvHotels.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View centerView = snapHelper.findSnapView(rvHotels.getLayoutManager());
                    if (centerView != null) {
                        int pos = rvHotels.getLayoutManager().getPosition(centerView);
                        if (pos >= 0 && pos < hotelList.size() && pos != selectedIndex) {
                            selectedIndex = pos;
                            displayHotels(); 
                            moveToHotel(hotelList.get(pos));
                        }
                    }
                }
            }
        });
    }

    private void displayHotels() {
        if (map == null || hotelList == null) return;
        map.getOverlays().clear();
        for (int i = 0; i < hotelList.size(); i++) {
            if (i == selectedIndex) continue;
            addMarkerToMap(i);
        }
        if (selectedIndex >= 0 && selectedIndex < hotelList.size()) {
            addMarkerToMap(selectedIndex);
        }
        map.invalidate();
    }

    private void addMarkerToMap(int index) {
        Hotel h = hotelList.get(index);
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(h.getLatitude(), h.getLongitude()));
        String priceLabel = formatPriceForMarker(h.getPrice());
        marker.setIcon(createPriceTagIcon(priceLabel, index == selectedIndex));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setOnMarkerClickListener((m, mapView) -> {
            if (selectedIndex != index) {
                selectedIndex = index;
                rvHotels.smoothScrollToPosition(index);
                displayHotels(); 
                moveToHotel(h);
            }
            return true;
        });
        map.getOverlays().add(marker);
    }

    private String formatPriceForMarker(double price) {
        if (price >= 1000000) return String.format(Locale.getDefault(), "%.1f Tr", price / 1000000.0);
        else if (price >= 1000) return (int)(price / 1000) + "k";
        return (int)price + "đ";
    }

    private void moveToHotel(Hotel hotel) {
        if (hotel != null) {
            GeoPoint point = new GeoPoint(hotel.getLatitude(), hotel.getLongitude());
            map.getController().animateTo(point);
            map.getController().setZoom(15.5);
        }
    }

    private Drawable createPriceTagIcon(String price, boolean isSelected) {
        try {
            View view = LayoutInflater.from(this).inflate(R.layout.marker_price_tag, null);
            View container = view.findViewById(R.id.markerContainer);
            TextView tv = view.findViewById(R.id.tvMarkerPrice);
            tv.setText(price);
            if (isSelected) {
                container.setBackgroundResource(R.drawable.bg_marker_pill_selected);
                tv.setTextColor(android.graphics.Color.WHITE);
            } else {
                container.setBackgroundResource(R.drawable.bg_marker_pill);
                tv.setTextColor(android.graphics.Color.parseColor("#333333"));
            }
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return new android.graphics.drawable.BitmapDrawable(getResources(), bitmap);
        } catch (Exception e) {
            return ContextCompat.getDrawable(this, android.R.drawable.ic_menu_myplaces);
        }
    }

    @Override protected void onResume() { super.onResume(); map.onResume(); }
    @Override protected void onPause() { super.onPause(); map.onPause(); }
}
