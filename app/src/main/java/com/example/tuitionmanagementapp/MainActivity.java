package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = new FirebaseHelper();

        firebaseHelper.writeData("message", "Hello from fixed-URL helper!", new FirebaseHelper.FirebaseCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Write successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, "Write failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        firebaseHelper.readData("message", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                Toast.makeText(MainActivity.this, "Read: " + value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MainActivity.this, "Read failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}