package com.example.doan1;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class TrainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        ImageButton btnBack = findViewById(R.id.btnBackTrain);
        btnBack.setOnClickListener(v -> finish());
    }
}
