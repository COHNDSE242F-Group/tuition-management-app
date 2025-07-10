package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tuitionmanagementapp.model.Student;
import com.example.tuitionmanagementapp.model.Teacher;

public class AdminRegStudentActivity extends AppCompatActivity {

    private EditText firstName,lastName,address,contactNumber,email;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_reg_student);

        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        address = findViewById(R.id.editTextAddress);
        contactNumber = findViewById(R.id.editTextContactNumber);
        email = findViewById(R.id.editTextEmail);


        String fName = firstName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String addr = address.getText().toString().trim();
        String contact = contactNumber.getText().toString().trim();
        String mail = email.getText().toString().trim();


        firebaseHelper = new FirebaseHelper();

        Student student = new Student("S001", fName, lName, addr, contact, mail);

        firebaseHelper.writeData("students/" + student.getStudentId(), student, new FirebaseHelper.FirebaseCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminRegStudentActivity.this, "Teacher saved!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminRegStudentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }



}
