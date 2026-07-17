package com.example.doan1;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        TextView tvBack = findViewById(R.id.tvBackToLoginFromForgot);
        tvBack.setOnClickListener(v -> finish());
    }
}