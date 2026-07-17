package com.example.doan1;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;

public class MapActivity extends AppCompatActivity {
    private MapView map;
    private final String MAPTILER_KEY = "5e1sYopkv7DLxzvoB8tq";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Thiết lập cấu hình osmdroid
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        
        setContentView(R.layout.activity_map);

        map = findViewById(R.id.mapView);
        map.setMultiTouchControls(true);

        // Cấu hình nguồn bản đồ từ MapTiler bằng API Key của bạn
        String mapUrl = "https://api.maptiler.com/maps/streets-v2/256/{z}/{x}/{y}.png?key=" + MAPTILER_KEY;
        
        map.setTileSource(new OnlineTileSourceBase("MapTiler", 1, 20, 256, ".png", new String[]{mapUrl}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl() 
                    .replace("{z}", String.valueOf(MapTileIndex.getZoom(pMapTileIndex)))
                    .replace("{x}", String.valueOf(MapTileIndex.getX(pMapTileIndex)))
                    .replace("{y}", String.valueOf(MapTileIndex.getY(pMapTileIndex)));
            }
        });

        // Đặt vị trí mặc định tại Hà Nội
        GeoPoint startPoint = new GeoPoint(21.0285, 105.8542);
        map.getController().setZoom(15.0);
        map.getController().setCenter(startPoint);

        // Nút quay lại
        ImageButton btnBack = findViewById(R.id.btnBackMap);
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}
