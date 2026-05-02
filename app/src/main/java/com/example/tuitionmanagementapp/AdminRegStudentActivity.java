package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuitionmanagementapp.model.Student;
import com.google.firebase.database.DataSnapshot;

public class AdminRegStudentActivity extends AppCompatActivity {

    private EditText firstName, lastName, address, contactNumber, email;
    private EditText age,  guardianName, guardianContact;

    private String userId;
    Spinner spinnerGender;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_reg_student);

        // Bind all input fields
        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        address = findViewById(R.id.editTextAddress);
        contactNumber = findViewById(R.id.editTextContactNumber);
        email = findViewById(R.id.editTextEmail);
         spinnerGender= findViewById(R.id.spinnerGender);

        age = findViewById(R.id.editTextAge);
        guardianName = findViewById(R.id.editTextGuardianName);
        guardianContact = findViewById(R.id.editTextGuardianContact);

        Button btnRegister = findViewById(R.id.btnregister);
        firebaseHelper = new FirebaseHelper();

        btnRegister.setOnClickListener(v -> generateNextStudentId());

        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show();
            finish();
        }


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);



        setupNavBar();
    }

    public void studentRegister(String studentId) {
        String fName = firstName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String addr = address.getText().toString().trim();
        String contact = contactNumber.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String Gender = spinnerGender.getSelectedItem().toString();
        String guardian = guardianName.getText().toString().trim();
        String guardianPhone = guardianContact.getText().toString().trim();

        int studentAge = 0;

        // password as firstName + "123"
        String password = fName + "123";
        try {
            studentAge = Integer.parseInt(age.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid age input", Toast.LENGTH_SHORT).show();
            return;
        }

        Student student = new Student(studentId, fName, lName, addr, contact, mail, studentAge, Gender, guardian, guardianPhone,password);

        firebaseHelper.writeData("students/" + studentId, student, new FirebaseHelper.FirebaseCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminRegStudentActivity.this, "Student registered successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminRegStudentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateNextStudentId() {
        firebaseHelper.getDatabase().getReference("students").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int maxId = 0;
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String id = snapshot.getKey(); // e.g. S001
                    if (id != null && id.startsWith("S")) {
                        try {
                            int num = Integer.parseInt(id.substring(1));
                            if (num > maxId) maxId = num;
                        } catch (NumberFormatException ignored) {}
                    }
                }
                String newStudentId = String.format("S%03d", maxId + 1);
                studentRegister(newStudentId);
            } else {
                Toast.makeText(this, "Failed to generate student ID", Toast.LENGTH_SHORT).show();
            }
        });
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

    }}

