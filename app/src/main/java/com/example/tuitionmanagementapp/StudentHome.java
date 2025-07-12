package com.example.tuitionmanagementapp;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class StudentHome extends AppCompatActivity {

    ImageView ivAttendance, ivAssignments, ivMaterials, ivSchedule;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        Intent parentIntent = getIntent();
        userId = parentIntent.getStringExtra("userId");

        ivAttendance = findViewById(R.id.ivAttendance);
        ivAssignments = findViewById(R.id.ivAssignments);
        ivMaterials = findViewById(R.id.ivMaterials);
        ivSchedule = findViewById(R.id.ivSchedule);

        ivAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentAttendanceActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        ivAssignments.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentAssignmentsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        ivMaterials.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentMaterialsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        ivSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentScheduleActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }
}

