package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminLoginActivity extends AppCompatActivity {


    private EditText editTextEmail, editTextPassword;
    private Button btnSignIn;
    private ImageButton btnTogglePassword;

    private static FirebaseHelper firebaseHelper;


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


        firebaseHelper = new FirebaseHelper();



        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        Toast.makeText(this, "AdminLoginActivity loaded", Toast.LENGTH_SHORT).show(); // TEMP




}
    private void loginUser() {
        Toast.makeText(this, "Login button clicked", Toast.LENGTH_SHORT).show();

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
                            Intent intent = new Intent(AdminLoginActivity.this, MainActivity.class);
                            intent.putExtra("userId", adminSnapshot.getKey());
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



}
