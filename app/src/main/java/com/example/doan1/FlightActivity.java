package com.example.doan1;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class FlightActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight);

        ImageButton btnBack = findViewById(R.id.btnBackFlight);
        btnBack.setOnClickListener(v -> finish());
    }
}
