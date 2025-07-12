package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {

    private Spinner spinnerStudentIds;
    private TextView textViewClass, textViewAttendance;
    private Button btnLogout;

    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance);

        // Bind UI components
        spinnerStudentIds = findViewById(R.id.spinnerStudentIds);
        textViewClass = findViewById(R.id.textViewClass);
        textViewAttendance = findViewById(R.id.textViewAttendance);
        btnLogout = findViewById(R.id.btnLogout);

        firebaseHelper = new FirebaseHelper();

        // Load all student IDs from Firebase
        loadStudentIds();

        // Logout button action
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AttendanceActivity.this, ParentHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadStudentIds() {
        firebaseHelper.readData("students", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                List<String> studentIds = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    studentIds.add(snap.getKey()); // e.g., "S001", "S002"
                }

                if (studentIds.isEmpty()) {
                    textViewClass.setText("No students found");
                    return;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        AttendanceActivity.this,
                        android.R.layout.simple_spinner_item,
                        studentIds
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStudentIds.setAdapter(adapter);

                // Set listener for selecting student
                spinnerStudentIds.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                        String selectedId = studentIds.get(position);
                        fetchStudentClass(selectedId);
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {
                        textViewClass.setText("");
                        textViewAttendance.setText("");
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                textViewClass.setText("❌ Failed to load student list");
            }
        });
    }

    private void fetchStudentClass(String studentId) {
        firebaseHelper.readData("student_class", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                String foundClassId = null;

                for (DataSnapshot classSnap : snapshot.getChildren()) {
                    DataSnapshot studentsSnap = classSnap.child("students");
                    if (studentsSnap.hasChild(studentId)) {
                        foundClassId = classSnap.child("class").getValue(String.class);
                        break;
                    }
                }

                if (foundClassId != null) {
                    textViewClass.setText("Class: " + foundClassId);
                    fetchAttendance(studentId, foundClassId);
                } else {
                    textViewClass.setText("Class not found");
                    textViewAttendance.setText("");
                }
            }

            @Override
            public void onError(Exception e) {
                textViewClass.setText("❌ Error fetching class info");
            }
        });
    }

    private void fetchAttendance(String studentId, String classId) {
        firebaseHelper.readData("attendance", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                StringBuilder attendanceList = new StringBuilder();

                for (DataSnapshot attendanceSnap : snapshot.getChildren()) {
                    String currentClassId = attendanceSnap.child("classId").getValue(String.class);
                    if (classId.equals(currentClassId)) {
                        if (attendanceSnap.child("students").hasChild(studentId)) {
                            String date = attendanceSnap.child("date").getValue(String.class);
                            attendanceList.append("• ").append(date).append("\n");
                        }
                    }
                }

                if (attendanceList.length() == 0) {
                    textViewAttendance.setText("No attendance records found.");
                } else {
                    textViewAttendance.setText("Attendance Dates:\n" + attendanceList.toString());
                }
            }

            @Override
            public void onError(Exception e) {
                textViewAttendance.setText("❌ Error loading attendance");
            }
        });
    }
}
