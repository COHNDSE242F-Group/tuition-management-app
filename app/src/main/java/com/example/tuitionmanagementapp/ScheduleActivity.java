package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

public class ScheduleActivity extends AppCompatActivity {

    TableLayout tableSchedule;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable); // timetable.xml layout

        tableSchedule = findViewById(R.id.tableSchedule);
        firebaseHelper = new FirebaseHelper();

        loadSchedule();

        // ✅ Logout Button logic
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ScheduleActivity.this, ParentHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadSchedule() {
        firebaseHelper.readData("schedule", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String classId = child.child("classId").getValue(String.class);
                    String date = child.child("date").getValue(String.class);
                    String startTime = child.child("startTime").getValue(String.class);
                    Long duration = child.child("duration").getValue(Long.class);

                    TableRow row = new TableRow(ScheduleActivity.this);

                    TextView tvClassId = new TextView(ScheduleActivity.this);
                    tvClassId.setText(classId);
                    tvClassId.setPadding(8, 8, 8, 8);

                    TextView tvDate = new TextView(ScheduleActivity.this);
                    tvDate.setText(date);
                    tvDate.setPadding(8, 8, 8, 8);

                    TextView tvStartTime = new TextView(ScheduleActivity.this);
                    tvStartTime.setText(startTime);
                    tvStartTime.setPadding(8, 8, 8, 8);

                    TextView tvDuration = new TextView(ScheduleActivity.this);
                    tvDuration.setText(String.valueOf(duration));
                    tvDuration.setPadding(8, 8, 8, 8);

                    row.addView(tvClassId);
                    row.addView(tvDate);
                    row.addView(tvStartTime);
                    row.addView(tvDuration);

                    tableSchedule.addView(row);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ScheduleActivity.this, "Failed to load schedule", Toast.LENGTH_SHORT).show();
                Log.e("ScheduleActivity", "Firebase Error", e);
            }
        });
    }
}
