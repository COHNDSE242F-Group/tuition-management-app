package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tuitionmanagementapp.model.Student;
import com.example.tuitionmanagementapp.model.Teacher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AdminRegStudentActivity extends AppCompatActivity {

    /*private EditText firstName,lastName,address,contactNumber,email;



    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_reg_student);




        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        contactNumber = findViewById(R.id.editTextContactNumber);
        email = findViewById(R.id.editTextEmail);

        Button btnRegister = findViewById(R.id.btnregister);

        address = findViewById(R.id.editTextAddress);


        firebaseHelper = new FirebaseHelper();

        btnRegister.setOnClickListener(v -> generateNextStudentId());



    }

    public void studentRegister(String studentId){
        String fName = firstName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String addr = address.getText().toString().trim();
        String contact = contactNumber.getText().toString().trim();
        String mail = email.getText().toString().trim();


       // String studentId = firebaseHelper.getDatabase().getReference("students").push().getKey();


        Student student = new Student(studentId, fName, lName, addr, contact, mail);

        firebaseHelper.writeData("students/" + student.getStudentId(), student, new FirebaseHelper.FirebaseCallback() {
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
                    String id = snapshot.getKey(); // S001
                    if (id != null && id.startsWith("S")) {
                        try {
                            int num = Integer.parseInt(id.substring(1));
                            if (num > maxId) maxId = num;
                        } catch (NumberFormatException e) {

                        }
                    }
                }

                int nextId = maxId + 1;
                String newStudentId = String.format("S%03d", nextId);
                studentRegister(newStudentId);

            } else {
                Toast.makeText(AdminRegStudentActivity.this, "Failed to generate student ID", Toast.LENGTH_SHORT).show();
            }
        });
    }
*/



}
