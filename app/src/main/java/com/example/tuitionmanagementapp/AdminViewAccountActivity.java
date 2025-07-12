package com.example.tuitionmanagementapp;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuitionmanagementapp.model.Student;
import com.example.tuitionmanagementapp.model.Teacher;
import com.google.firebase.database.DataSnapshot;

public class AdminViewAccountActivity extends AppCompatActivity {

    private static final String TAG = "AdminViewAccount";

    private LinearLayout studentContainer, teacherContainer;
    private FirebaseHelper firebaseHelper;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_accounts);

        studentContainer = findViewById(R.id.studentContainer);
        teacherContainer = findViewById(R.id.teacherContainer);

        firebaseHelper = new FirebaseHelper();

        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadStudents();
        loadTeachers();
        setupNavBar();
    }

    private void loadStudents() {
        firebaseHelper.readData("students", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                studentContainer.removeAllViews();

                if (!snapshot.exists()) {
                    Toast.makeText(AdminViewAccountActivity.this, "No students found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Student student = snap.getValue(Student.class);
                    if (student != null) {
                        Log.d(TAG, "Student loaded: " + student.getFirstname() + " " + student.getLastname());
                        View studentView = createUserCard(
                                student.getFirstname() + " " + student.getLastname(),
                                student.getEmail(),
                                student.getContactNo(),
                                student.getHomeaddress(),
                                "Age: " + student.getAge(),
                                "#E0F7FA"
                        );
                        studentContainer.addView(studentView);
                    } else {
                        Log.w(TAG, "Student data is null for key: " + snap.getKey());
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminViewAccountActivity.this, "Failed to load students: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error loading students", e);
            }
        });
    }

    private void loadTeachers() {
        firebaseHelper.readData("teachers", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                teacherContainer.removeAllViews();

                if (!snapshot.exists()) {
                    Toast.makeText(AdminViewAccountActivity.this, "No teachers found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Teacher teacher = snap.getValue(Teacher.class);
                    if (teacher != null) {
                        Log.d(TAG, "Teacher loaded: " + teacher.getFirstName() + " " + teacher.getLastName());
                        View teacherView = createUserCard(
                                teacher.getFirstName() + " " + teacher.getLastName(),
                                teacher.getEmail(),
                                teacher.getContactNo(),
                                teacher.getHomeaddress(),
                                "Subject: " + (teacher.getSubject() != null ? teacher.getSubject() : "N/A"),
                                "#FFF3E0"
                        );
                        teacherContainer.addView(teacherView);
                    } else {
                        Log.w(TAG, "Teacher data is null for key: " + snap.getKey());
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminViewAccountActivity.this, "Failed to load teachers: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error loading teachers", e);
            }
        });
    }

    private View createUserCard(String name, String email, String contact, String address, String extraInfo, String bgColor) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 24, 32, 24);
        layout.setBackgroundColor(Color.parseColor(bgColor));
        layout.setElevation(6);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 20);
        layout.setLayoutParams(params);

        TextView tvName = new TextView(this);
        tvName.setText("Name: " + name);
        tvName.setTextSize(18);
        tvName.setTextColor(Color.BLACK);

        TextView tvEmail = new TextView(this);
        tvEmail.setText("Email: " + email);
        tvEmail.setTextSize(16);
        tvEmail.setTextColor(Color.DKGRAY);

        TextView tvContact = new TextView(this);
        tvContact.setText("Contact: " + contact);
        tvContact.setTextSize(16);
        tvContact.setTextColor(Color.DKGRAY);

        TextView tvAddress = new TextView(this);
        tvAddress.setText("Address: " + address);
        tvAddress.setTextSize(16);
        tvAddress.setTextColor(Color.DKGRAY);

        TextView tvExtra = new TextView(this);
        tvExtra.setText(extraInfo);
        tvExtra.setTextSize(16);
        tvExtra.setTextColor(Color.DKGRAY);

        layout.addView(tvName);
        layout.addView(tvEmail);
        layout.addView(tvContact);
        layout.addView(tvAddress);
        layout.addView(tvExtra);

        return layout;
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
