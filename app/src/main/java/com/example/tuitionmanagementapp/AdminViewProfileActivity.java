package com.example.tuitionmanagementapp;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
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

    private TextView textName, textAge, textGender, textContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view_profile);  // Replace with your actual layout name if different

        // Initialize views
        textName = findViewById(R.id.textName);
        textAge = findViewById(R.id.textAge);
        textGender = findViewById(R.id.textGender);
        textContact = findViewById(R.id.textContact);

        // Get userId from Intent
        userId = getIntent().getStringExtra("userId");

        if (userId != null && !userId.isEmpty()) {
            databaseRef = FirebaseDatabase.getInstance()
                    .getReference("admin")
                    .child(userId)
                    .child("personal_info");

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String age = snapshot.child("age").getValue() != null ? snapshot.child("age").getValue().toString() : "N/A";
                        String gender = snapshot.child("gender").getValue(String.class);
                        String contact = snapshot.child("contact").getValue() != null ? snapshot.child("contact").getValue().toString() : "N/A";

                        textName.setText(name != null ? name : "N/A");
                        textAge.setText(age != null ? age : "N/A");
                        textGender.setText(gender != null ? gender : "N/A");
                        textContact.setText(contact != null ? contact : "N/A");
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
        LinearLayout navRegister = findViewById(R.id.navRegister);
        LinearLayout navStudent=findViewById(R.id.navStudent);
        LinearLayout navProfile=findViewById(R.id.navProfile);


        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminLoginActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });


        navRegister.setOnClickListener(v -> {
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
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminViewProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });



    }
}

