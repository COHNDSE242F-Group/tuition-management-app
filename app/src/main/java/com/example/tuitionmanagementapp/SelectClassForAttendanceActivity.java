package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

public class SelectClassForAttendanceActivity extends AppCompatActivity {

    private LinearLayout layoutClassList;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_class);

        layoutClassList = findViewById(R.id.layoutClassList);
        firebaseHelper = new FirebaseHelper();

        loadAttendanceRecords();
    }

    private void loadAttendanceRecords() {
        firebaseHelper.readData("attendance", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(SelectClassForAttendanceActivity.this, "No attendance records found", Toast.LENGTH_SHORT).show();
                    return;
                }

                layoutClassList.removeAllViews();

                for (DataSnapshot attendanceSnap : snapshot.getChildren()) {
                    String attendanceId = attendanceSnap.getKey();
                    String classId = attendanceSnap.child("classId").getValue(String.class);
                    String date = attendanceSnap.child("date").getValue(String.class);

                    // Handle possible nulls
                    if (classId == null) classId = "N/A";
                    if (date == null) date = "Unknown Date";

                    StringBuilder studentListBuilder = new StringBuilder();
                    DataSnapshot studentsSnap = attendanceSnap.child("students");
                    for (DataSnapshot studentSnap : studentsSnap.getChildren()) {
                        String studentId = studentSnap.getKey();
                        studentListBuilder.append("\t\t\t\t• ").append(studentId).append("\n");
                    }

                    addAttendanceCard(attendanceId, classId, date, studentListBuilder.toString().trim());
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(SelectClassForAttendanceActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addAttendanceCard(String attendanceId, String classId, String date, String studentList) {
        TextView tv = new TextView(this);
        String content = "Attendance ID: " + attendanceId +
                "\nClass ID: " + classId +
                "\nDate: " + date +
                "\nStudents:\n\t\t\t\t" + studentList;

        tv.setText(content);
        tv.setTextSize(16f);
        tv.setPadding(30, 30, 30, 30);
        tv.setBackgroundResource(R.drawable.card_background); // Optional: create a drawable for styling
        tv.setTextColor(getResources().getColor(android.R.color.black));

        layoutClassList.addView(tv);
    }
}