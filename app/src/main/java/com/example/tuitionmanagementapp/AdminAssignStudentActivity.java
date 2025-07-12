package com.example.tuitionmanagementapp;

import android.content.Intent;
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

import androidx.appcompat.app.AlertDialog;
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



    private List<Student> allStudents = new ArrayList<>();

    private List<Map<String, Object>> classList = new ArrayList<>();

    private List<Student> filteredStudents = new ArrayList<>();

    private EditText etSearch;
    private Button btnSortGrade;
    private String userId;

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

        loadClassesAndStudents();
        setupNavBar();

        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void loadClassesAndStudents() {
        firebaseHelper.getDatabase().getReference("classes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                classList.clear();
                for (DataSnapshot snap : task.getResult().getChildren()) {
                    Map<String, Object> cls = (Map<String, Object>) snap.getValue();
                    classList.add(cls);
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

            Button btnAssign = new Button(this);
            btnAssign.setText("Assign Classes");
            btnAssign.setOnClickListener(v -> showMultiClassDialog(student));


            container.addView(tvInfo);
            container.addView(btnAssign);

            layoutStudentList.addView(container);
        }
    }

    private void showMultiClassDialog(Student student) {
        String[] classNames = new String[classList.size()];
        boolean[] selectedItems = new boolean[classList.size()];

        for (int i = 0; i < classList.size(); i++) {
            Map<String, Object> cls = classList.get(i);
            classNames[i] = "Grade " + cls.get("grade") + " - " + cls.get("subject");
        }

        new AlertDialog.Builder(this)
                .setTitle("Assign Classes to " + student.getFirstname())
                .setMultiChoiceItems(classNames, selectedItems, (dialog, which, isChecked) -> {
                    selectedItems[which] = isChecked;
                })
                .setPositiveButton("Assign", (dialog, which) -> {
                    for (int i = 0; i < selectedItems.length; i++) {
                        if (selectedItems[i]) {
                            Map<String, Object> cls = classList.get(i);
                            String classId = (String) cls.get("classId");
                            String stcKey = "STC_" + classId;

                            // Add this student under that class assignment
                            firebaseHelper.getDatabase()
                                    .getReference("student_class/" + stcKey + "/class").setValue(classId);

                            firebaseHelper.getDatabase()
                                    .getReference("student_class/" + stcKey + "/students/" + student.getStudentId())
                                    .setValue("");
                        }
                    }

                    Toast.makeText(this, "Classes assigned!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    public void setupNavBar() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navStudent = findViewById(R.id.navStudent);
        LinearLayout navAccounts=findViewById(R.id.navAccounts);
        LinearLayout navProfile=findViewById(R.id.navProfile);


        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminHomeActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });


        navStudent.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminAssignStudentActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });

        navAccounts.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminViewAccountActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminViewProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });




    }


}
