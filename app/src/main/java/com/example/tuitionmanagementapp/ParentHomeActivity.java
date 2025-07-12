package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ParentHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_home);

        // Button setup
        Button btnAttendance = findViewById(R.id.btnAttendance);
        Button btnFeeStatus = findViewById(R.id.btnFeeStatus);
        Button btnAssignments = findViewById(R.id.btnAssignments);
        Button btnResults = findViewById(R.id.btnResults);
        Button btnTimetable = findViewById(R.id.btnTimetable);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnAppointment = findViewById(R.id.btnappointment);
        Button btnResponse = findViewById(R.id.btnResponse);
        Button btnAddNotification = findViewById(R.id.btnAddNotification);

        // Button listeners
        btnAttendance.setOnClickListener(v -> startActivity(new Intent(this, AttendanceActivity.class)));
        btnFeeStatus.setOnClickListener(v -> startActivity(new Intent(this, FeeStatusActivity.class)));
        btnAssignments.setOnClickListener(v -> startActivity(new Intent(this, AssignmentActivity.class)));
        btnResults.setOnClickListener(v -> startActivity(new Intent(this, ResultActivity.class)));
        btnAppointment.setOnClickListener(v -> startActivity(new Intent(this, AppointmentActivity.class)));
        btnResponse.setOnClickListener(v -> startActivity(new Intent(this, AppointmentResponseActivity.class)));
        btnAddNotification.setOnClickListener(v -> startActivity(new Intent(this, AddNotificationActivity.class)));

        btnTimetable.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScheduleActivity.class);
            intent.putExtra("childId", "C001"); // default or hardcoded ID
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> finishAffinity());
    }
}
