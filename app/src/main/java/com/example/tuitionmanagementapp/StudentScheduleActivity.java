package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StudentScheduleActivity extends AppCompatActivity {

    LinearLayout layoutSchedule;
    FirebaseHelper firebaseHelper;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_schedule);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        if (userId == null || userId.trim().isEmpty()) {
            Toast.makeText(this, "No userId provided", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        layoutSchedule = findViewById(R.id.layoutSchedule);
        firebaseHelper = new FirebaseHelper();

        loadStudentSchedule();
    }

    private void loadStudentSchedule() {
        firebaseHelper.readData("student_class", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                List<String> classIds = new ArrayList<>();

                for (DataSnapshot scSnapshot : snapshot.getChildren()) {
                    String classId = scSnapshot.child("class").getValue(String.class);
                    DataSnapshot studentsSnapshot = scSnapshot.child("students");

                    if (studentsSnapshot.hasChild(userId)) {
                        classIds.add(classId);
                    }
                }

                loadSchedulesForClasses(classIds);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(StudentScheduleActivity.this, "Failed to load student classes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSchedulesForClasses(List<String> classIds) {
        firebaseHelper.readData("schedule", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                layoutSchedule.removeAllViews();

                for (DataSnapshot schSnapshot : snapshot.getChildren()) {
                    String classId = schSnapshot.child("classId").getValue(String.class);
                    String date = schSnapshot.child("date").getValue(String.class);
                    String startTime = schSnapshot.child("startTime").getValue(String.class);
                    Integer duration = schSnapshot.child("duration").getValue(Integer.class);

                    if (classId != null && classIds.contains(classId)) {
                        fetchSubjectAndDisplay(classId, date, startTime, duration);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(StudentScheduleActivity.this, "Failed to load schedules", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSubjectAndDisplay(String classId, String date, String startTime, Integer duration) {
        firebaseHelper.readData("classes/" + classId, new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot classSnapshot) {
                String subject = classSnapshot.child("subject").getValue(String.class);
                String timeRange = formatTimeRange(startTime, duration);
                addScheduleCard(date, subject != null ? subject : "Unknown", timeRange);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(StudentScheduleActivity.this, "Failed to load subject", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatTimeRange(String startTime, Integer durationHours) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(startTime));

            String startFormatted = sdf.format(calendar.getTime());

            if (durationHours != null) {
                calendar.add(Calendar.HOUR_OF_DAY, durationHours);
            }

            String endFormatted = sdf.format(calendar.getTime());
            return startFormatted + " - " + endFormatted;
        } catch (Exception e) {
            return startTime + " - ?";
        }
    }

    private void addScheduleCard(String date, String subject, String time) {
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
        tvDate.setText("📅 " + date);
        tvDate.setTextSize(18f);
        tvDate.setTextColor(getResources().getColor(android.R.color.black));

        TextView tvTime = new TextView(this);
        tvTime.setText("🕒 " + time);
        tvTime.setTextSize(16f);
        tvTime.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));

        TextView tvSubject = new TextView(this);
        tvSubject.setText("📖 Subject: " + subject);
        tvSubject.setTextSize(16f);
        tvSubject.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

        innerLayout.addView(tvDate);
        innerLayout.addView(tvTime);
        innerLayout.addView(tvSubject);

        card.addView(innerLayout);
        layoutSchedule.addView(card);
    }
}