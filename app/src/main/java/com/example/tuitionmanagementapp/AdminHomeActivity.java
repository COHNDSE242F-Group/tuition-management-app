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

        findViewById(R.id.AttendenceCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectClassForAttendanceActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.ResultsCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectClassForExamMarksActivity.class);
            startActivity(intent);
        });



    }


    public void setupNavBar() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navStudent = findViewById(R.id.navStudent);
        LinearLayout navAccounts=findViewById(R.id.navAccounts);
        LinearLayout navProfile=findViewById(R.id.navProfile);


        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminHomeActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });


        navStudent.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminAssignStudentActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });

        navAccounts.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminViewAccountActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminViewProfileActivity.class);
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
