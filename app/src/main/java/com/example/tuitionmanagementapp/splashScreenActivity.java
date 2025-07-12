package com.example.tuitionmanagementapp;



import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class splashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Wait a bit then launch main activity
        new android.os.Handler().postDelayed(() -> {
            startActivity(new Intent(splashScreenActivity.this, AdminLoginActivity.class));
            finish();
        }, 2000); // 2 seconds splash
    }
}