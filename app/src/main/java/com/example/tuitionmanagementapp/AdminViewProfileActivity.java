package com.example.tuitionmanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminViewProfileActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    private String userId;

    private TextView textName, textAge,  textContact, textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view_profile);

        // Initialize views
        textName = findViewById(R.id.textName);
        textAge = findViewById(R.id.textAge);

        textContact = findViewById(R.id.textContact);
        textEmail = findViewById(R.id.textEmail);

        // Get userId from Intent
        userId = getIntent().getStringExtra("userId");


        setupNavBar();

        findViewById(R.id.btnLogout).setOnClickListener(v -> {

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AdminLoginActivity.class); // Change to your login screen
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        if (userId != null && !userId.isEmpty()) {
            databaseRef = FirebaseDatabase.getInstance("https://tuition-management-syste-a31c0-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("admin")
                    .child(userId);


            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String age = snapshot.child("age").getValue() != null ? snapshot.child("age").getValue().toString() : "N/A";
                        String contact = snapshot.child("contact").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);



                        textName.setText(name != null ? name : "N/A");
                        textAge.setText(age != null ? age : "N/A");
                        textContact.setText(contact != null ? contact : "N/A");
                        textEmail.setText(email != null ? email : "N/A");
                    } else {
                        Toast.makeText(AdminViewProfileActivity.this, "No profile data found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AdminViewProfileActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User ID not found. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupNavBar();
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

