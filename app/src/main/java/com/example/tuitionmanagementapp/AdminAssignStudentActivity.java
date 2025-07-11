package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuitionmanagementapp.model.Student;
import com.example.tuitionmanagementapp.model.Teacher;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAssignStudentActivity extends AppCompatActivity {

    private LinearLayout layoutStudentList;
    private FirebaseHelper firebaseHelper;


    private List<Teacher> teacherList = new ArrayList<>();
    private List<Student> allStudents = new ArrayList<>();
    private List<Student> filteredStudents = new ArrayList<>();

    private EditText etSearch;
    private Button btnSortGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_assign_student);

        layoutStudentList = findViewById(R.id.layoutStudentList);

        etSearch = findViewById(R.id.etSearch);

        btnSortGrade = findViewById(R.id.btnSortGrade);
        firebaseHelper = new FirebaseHelper();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });



        btnSortGrade.setOnClickListener(v -> {
            Collections.sort(filteredStudents, Comparator.comparing(Student::getAge));
            displayStudentRows();
        });

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
                allStudents.clear();
                for (DataSnapshot snap : task.getResult().getChildren()) {
                    Student student = snap.getValue(Student.class);
                    allStudents.add(student);
                }
                filteredStudents.clear();
                filteredStudents.addAll(allStudents);
                displayStudentRows();
            }
        });
    }


    private void filterStudents(String query) {
        filteredStudents.clear();
        for (Student s : allStudents) {
            String fullName = (s.getFirstname() + " " + s.getLastname()).toLowerCase();
            if (fullName.contains(query.toLowerCase())) {
                filteredStudents.add(s);
            }
        }
        displayStudentRows();
    }

    private void displayStudentRows() {
        layoutStudentList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Student student : filteredStudents) {
            LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setPadding(24, 24, 24, 24);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 24);
            container.setLayoutParams(params);

            container.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

            TextView tvInfo = new TextView(this);
            tvInfo.setText("Name: " + student.getFirstname() + " " + student.getLastname()

                    + "\nGrade: " + student.getAge());
            tvInfo.setTextSize(16f);
            tvInfo.setPadding(0, 0, 0, 12);

            Spinner spinner = new Spinner(this);
            List<String> teacherNames = new ArrayList<>();
            for (Teacher t : teacherList) {
                teacherNames.add(t.getFirstName() + " - " + t.getSubject());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, teacherNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

            container.addView(tvInfo);
            container.addView(spinner);

            layoutStudentList.addView(container);
        }
    }
}
