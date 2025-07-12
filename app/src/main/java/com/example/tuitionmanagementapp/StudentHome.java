package com.example.tuitionmanagementapp;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class StudentHome extends AppCompatActivity {

    ImageView ivAttendance, ivAssignments, ivMaterials, ivSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        ivAttendance = findViewById(R.id.ivAttendance);
        ivAssignments = findViewById(R.id.ivAssignments);
        ivMaterials = findViewById(R.id.ivMaterials);
        ivSchedule = findViewById(R.id.ivSchedule);

        ivAttendance.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentAttendanceActivity.class));
        });

        ivAssignments.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentAssignmentsActivity.class));
        });

        ivMaterials.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentMaterialsActivity.class));
        });

        ivSchedule.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentScheduleActivity.class));
        });
    }
}

