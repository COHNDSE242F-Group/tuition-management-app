package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentAttendanceActivity extends AppCompatActivity {

    LinearLayout layoutAttendance;
    FirebaseHelper firebaseHelper;
    String userId; // e.g., "S001"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        if (userId == null || userId.trim().isEmpty()) {
            Toast.makeText(this, "No userId provided in Intent", Toast.LENGTH_LONG).show();
            finish(); // close the activity gracefully
            return;
        }

        layoutAttendance = findViewById(R.id.layoutAttendance);
        firebaseHelper = new FirebaseHelper();

        loadStudentClassIds();
    }

    private void loadStudentClassIds() {
        firebaseHelper.readData("student_class", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                List<String> enrolledClassIds = new ArrayList<>();

                for (DataSnapshot scSnapshot : snapshot.getChildren()) {
                    String classId = scSnapshot.child("class").getValue(String.class);
                    DataSnapshot studentsSnapshot = scSnapshot.child("students");

                    if (studentsSnapshot.hasChild(userId)) {
                        enrolledClassIds.add(classId);
                    }
                }

                checkAttendanceForClasses(enrolledClassIds);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(StudentAttendanceActivity.this, "Error loading student_class: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAttendanceForClasses(List<String> classIds) {
        firebaseHelper.readData("attendance", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                layoutAttendance.removeAllViews(); // clear previous views

                Map<String, Boolean> processed = new HashMap<>();

                for (DataSnapshot attSnapshot : snapshot.getChildren()) {
                    String classId = attSnapshot.child("classId").getValue(String.class);
                    String date = attSnapshot.child("date").getValue(String.class);

                    if (classId == null || date == null) continue;

                    if (classIds.contains(classId)) {
                        boolean isPresent = attSnapshot.child("students").hasChild(userId);
                        String status = isPresent ? "Present" : "Absent";

                        // Avoid duplicate display (optional safeguard)
                        String uniqueKey = classId + "_" + date;
                        if (!processed.containsKey(uniqueKey)) {
                            addAttendanceCard("Class " + classId + " - " + date, status);
                            processed.put(uniqueKey, true);
                        }
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(StudentAttendanceActivity.this, "Error loading attendance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addAttendanceCard(String title, String status) {
        CardView card = new CardView(this);
        card.setCardElevation(6);
        card.setRadius(16);
        card.setUseCompatPadding(true);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16);
        card.setLayoutParams(cardParams);

        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(24, 24, 24, 24);

        TextView tvDate = new TextView(this);
        tvDate.setText("📅 " + title);
        tvDate.setTextSize(16f);
        tvDate.setTextColor(getResources().getColor(android.R.color.black));

        TextView tvStatus = new TextView(this);
        tvStatus.setText("Status: " + status);
        tvStatus.setTextSize(16f);
        tvStatus.setTextColor(status.equalsIgnoreCase("Present") ?
                getResources().getColor(android.R.color.holo_green_dark) :
                getResources().getColor(android.R.color.holo_red_dark));
        tvStatus.setGravity(Gravity.END);

        innerLayout.addView(tvDate);
        innerLayout.addView(tvStatus);

        card.addView(innerLayout);
        layoutAttendance.addView(card);
    }
}