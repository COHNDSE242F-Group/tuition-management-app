package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuitionmanagementapp.model.Teacher;
import com.google.firebase.database.DataSnapshot;

public class AdminRegTeacher extends AppCompatActivity {

    private EditText firstName, lastName, address, contactNumber, email;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_reg_teacher); 

        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        address = findViewById(R.id.editTextAddress);
        contactNumber = findViewById(R.id.editTextContactNumber);
        email = findViewById(R.id.editTextEmail);

        Button btnRegister = findViewById(R.id.btnregister);

        firebaseHelper = new FirebaseHelper();

        btnRegister.setOnClickListener(v -> generateNextTeacherId());
    }

    public void registerTeacher(String teacherId){
        String fName = firstName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String addr = address.getText().toString().trim();
        String contact = contactNumber.getText().toString().trim();
        String mail = email.getText().toString().trim();

        Teacher teacher = new Teacher(teacherId, fName, lName, addr, contact, mail);

        firebaseHelper.writeData("teachers/" + teacher.getTeacherId(), teacher, new FirebaseHelper.FirebaseCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminRegTeacher.this, "Teacher registered successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminRegTeacher.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateNextTeacherId() {
        firebaseHelper.getDatabase().getReference("teachers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int maxId = 0;
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String id = snapshot.getKey(); // T001
                    if (id != null && id.startsWith("T")) {
                        try {
                            int num = Integer.parseInt(id.substring(1));
                            if (num > maxId) maxId = num;
                        } catch (NumberFormatException ignored) {}
                    }
                }

                int nextId = maxId + 1;
                String newTeacherId = String.format("T%03d", nextId);
                registerTeacher(newTeacherId);

            } else {
                Toast.makeText(AdminRegTeacher.this, "Failed to generate teacher ID", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
