package com.example.doan1;

import android.app.Application;
import androidx.preference.PreferenceManager;
import org.osmdroid.config.Configuration;
import java.io.File;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Cấu hình OSMDroid tập trung
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        
        // Sử dụng User-Agent mô phỏng trình duyệt để tránh bị chặn
        Configuration.getInstance().setUserAgentValue("Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Mobile Safari/537.36");

        // Thiết lập thư mục Cache
        File cacheDir = new File(getCacheDir(), "osmdroid_tiles");
        if (!cacheDir.exists()) cacheDir.mkdirs();
        Configuration.getInstance().setOsmdroidTileCache(cacheDir);
    }
}
