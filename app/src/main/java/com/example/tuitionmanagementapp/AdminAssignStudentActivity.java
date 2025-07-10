package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuitionmanagementapp.model.Student;
import com.example.tuitionmanagementapp.model.Teacher;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAssignStudentActivity extends AppCompatActivity {

    private LinearLayout layoutStudentList;
    private FirebaseHelper firebaseHelper;

    private List<Teacher> teacherList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_assign_student);

        layoutStudentList = findViewById(R.id.layoutStudentList);
        firebaseHelper = new FirebaseHelper();

        loadTeachersAndStudents();
    }

    private void loadTeachersAndStudents() {
        // Step 1: Load teachers first
        firebaseHelper.getDatabase().getReference("teachers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                teacherList.clear();
                for (DataSnapshot snap : task.getResult().getChildren()) {
                    Teacher t = snap.getValue(Teacher.class);
                    teacherList.add(t);
                }
                loadStudents();
            }
        });
    }

    private void loadStudents() {
        firebaseHelper.getDatabase().getReference("students").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                layoutStudentList.removeAllViews(); // Clear before adding

                for (DataSnapshot snap : task.getResult().getChildren()) {
                    Student student = snap.getValue(Student.class);
                    addStudentRow(student);
                }
            }
        });
    }

    private void addStudentRow(Student student) {
        // Create layout
       ;

        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.VERTICAL);
        rowLayout.setPadding(0, 0, 0, 24);

        // Student details
        TextView studentText = new TextView(this);
        studentText.setText("Name: " + student.getFirstname() + " " + student.getLastname()
                + "\nAge: " + student.getAge());

        studentText.setTextSize(16f);

        // Spinner
        Spinner teacherSpinner = new Spinner(this);
        List<String> teacherNames = new ArrayList<>();
        for (Teacher t : teacherList) {
            teacherNames.add(t.getFirstName() + " - " + t.getSubject());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(adapter);

        // Spinner listener to assign
        teacherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstLoad = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstLoad) {
                    firstLoad = false;
                    return;
                }

                Teacher selectedTeacher = teacherList.get(position);
                Map<String, Object> assignment = new HashMap<>();
                assignment.put("teacherId", selectedTeacher.getTeacherId());

                firebaseHelper.getDatabase().getReference("student_teacher_assignments/" + student.getStudentId())
                        .setValue(assignment)
                        .addOnSuccessListener(unused -> Toast.makeText(AdminAssignStudentActivity.this, "Assigned!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(AdminAssignStudentActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Add views to layout
        rowLayout.addView(studentText);
        rowLayout.addView(teacherSpinner);

        layoutStudentList.addView(rowLayout);
    }
}
