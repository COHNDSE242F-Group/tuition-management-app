package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AdminHomeActivity extends AppCompatActivity {


    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_home);


        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setupNavBar();
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));



    }


    public void setupNavBar() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navRegister = findViewById(R.id.navRegister);


        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminLoginActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });


        navRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminHomeActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });




    }
    public void loadStudentRegister(View view){
        LinearLayout studentCard =findViewById(R.id.studentCard);


        studentCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminRegStudentActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });


    }
    
    public void loadTeacherRegister(View view){
        LinearLayout teacherCard= findViewById(R.id.teacherCard);
        teacherCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminRegTeacher.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });
    }
}
