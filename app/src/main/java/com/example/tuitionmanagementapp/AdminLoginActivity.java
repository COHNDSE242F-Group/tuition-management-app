package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminLoginActivity extends AppCompatActivity {


    private EditText editTextEmail, editTextPassword;
    private Button btnSignIn;
    private ImageButton btnTogglePassword;

    private static FirebaseHelper firebaseHelper;
    private Spinner spinnerRole;

    private String selectedRole = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_login);


      //  firebaseHelper=FirebaseHelper.getInstance();

        editTextEmail =findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        btnSignIn=findViewById(R.id.btnSignIn);
        btnTogglePassword=findViewById(R.id.btnTogglePassword);
        spinnerRole = findViewById(R.id.spinnerRole);



        firebaseHelper = new FirebaseHelper();


        togglePassword();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.user_roles, // Defined in strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString(); // Save role
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = "";
            }
        });



}
    private void loginUser() {

        String inputEmail = editTextEmail.getText().toString().trim();
        String inputPW = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(inputEmail)) {
            editTextEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(inputPW)) {
            editTextPassword.setError("Password is required");
            return;
        }




        if (selectedRole.equals("Admin")) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tuition-management-syste-a31c0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("admin");
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    boolean found = false;
                    for (DataSnapshot adminSnapshot : snapshot.getChildren()) {
                        String dbEmail = adminSnapshot.child("email").getValue(String.class);
                        String dbPassword = adminSnapshot.child("password").getValue(String.class);


                        if (dbEmail != null && dbPassword != null &&
                                inputEmail.equals(dbEmail) && inputPW.equals(dbPassword)) {
                            Toast.makeText(AdminLoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();


                            String passedUserId = adminSnapshot.getKey();

                            Intent intent = new Intent(AdminLoginActivity.this, AdminHomeActivity.class);
                            intent.putExtra("userId", passedUserId);


                            startActivity(intent);

                            finish();
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Toast.makeText(AdminLoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Admin data node empty or missing!", Toast.LENGTH_LONG).show();

                }
            } else {
                Exception e = task.getException();
                Toast.makeText(this, "Firebase error: " + (e != null ? e.getMessage() : "Unknown"), Toast.LENGTH_LONG).show();

            }
        });
    }

        if (selectedRole.equals("Teacher")) {
            DatabaseReference ref = FirebaseDatabase.getInstance("https://tuition-management-syste-a31c0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("teachers");
            ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        boolean found = false;
                        for (DataSnapshot adminSnapshot : snapshot.getChildren()) {
                            String dbEmail = adminSnapshot.child("email").getValue(String.class);
                            String dbPassword = adminSnapshot.child("password").getValue(String.class);

                            if (dbEmail != null && dbPassword != null &&
                                    inputEmail.equals(dbEmail) && inputPW.equals(dbPassword)) {
                                Toast.makeText(AdminLoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();


                                String passedUserId = adminSnapshot.getKey();

                                Intent intent = new Intent(AdminLoginActivity.this, TeacherHomeActivity.class);
                                intent.putExtra("userId", passedUserId);


                                startActivity(intent);
                                finish();
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            Toast.makeText(AdminLoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Teacher data node empty or missing!", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Exception e = task.getException();
                    Toast.makeText(this, "Firebase error: " + (e != null ? e.getMessage() : "Unknown"), Toast.LENGTH_LONG).show();

                }
            });
        }




        if (selectedRole.equals("Student")) {
            DatabaseReference ref = FirebaseDatabase.getInstance("https://tuition-management-syste-a31c0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("students");
            ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        boolean found = false;
                        for (DataSnapshot adminSnapshot : snapshot.getChildren()) {
                            String dbEmail = adminSnapshot.child("email").getValue(String.class);
                            String dbPassword = adminSnapshot.child("password").getValue(String.class);

                            if (dbEmail != null && dbPassword != null &&
                                    inputEmail.equals(dbEmail) && inputPW.equals(dbPassword)) {
                                Toast.makeText(AdminLoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();


                                String passedUserId = adminSnapshot.getKey();

                                Intent intent = new Intent(AdminLoginActivity.this, AdminHomeActivity.class);
                                intent.putExtra("userId", passedUserId);


                                startActivity(intent);
                                finish();
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            Toast.makeText(AdminLoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Student data node empty or missing!", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Exception e = task.getException();
                    Toast.makeText(this, "Firebase error: " + (e != null ? e.getMessage() : "Unknown"), Toast.LENGTH_LONG).show();

                }
            });
        }

        if (selectedRole.equals("Parent")) {
            DatabaseReference ref = FirebaseDatabase.getInstance("https://tuition-management-syste-a31c0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("students");
            ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        boolean found = false;
                        for (DataSnapshot adminSnapshot : snapshot.getChildren()) {
                            String dbEmail = adminSnapshot.child("email").getValue(String.class);
                            String dbPassword = adminSnapshot.child("password").getValue(String.class);

                            if (dbEmail != null && dbPassword != null &&
                                    inputEmail.equals(dbEmail) && inputPW.equals(dbPassword)) {
                                Toast.makeText(AdminLoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();


                                String passedUserId = adminSnapshot.getKey();

                                Intent intent = new Intent(AdminLoginActivity.this, AdminHomeActivity.class);
                                intent.putExtra("userId", passedUserId);


                                startActivity(intent);
                                finish();
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            Toast.makeText(AdminLoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Parent data node empty or missing!", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Exception e = task.getException();
                    Toast.makeText(this, "Firebase error: " + (e != null ? e.getMessage() : "Unknown"), Toast.LENGTH_LONG).show();

                }
            });
        }
    }
    public void togglePassword() {
        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            private boolean isPasswordVisible = false;

            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide password
                    editTextPassword.setInputType(
                            android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnTogglePassword.setBackgroundResource(android.R.drawable.ic_menu_view); // example icon
                } else {
                    // Show password
                    editTextPassword.setInputType(
                            android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnTogglePassword.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel); // example icon
                }
                // Move cursor to end
                editTextPassword.setSelection(editTextPassword.getText().length());

                isPasswordVisible = !isPasswordVisible;
            }
        });


    }
}
