package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AppointmentActivity extends AppCompatActivity {

    EditText editDescription;
    Button btnRequestAppointment, btnBack;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment);

        editDescription = findViewById(R.id.editDescription);
        btnRequestAppointment = findViewById(R.id.btnappointment);
        btnBack = findViewById(R.id.btnBack);
        firebaseHelper = new FirebaseHelper();

        btnRequestAppointment.setOnClickListener(v -> {
            String description = editDescription.getText().toString().trim();
            if (description.isEmpty()) {
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
                return;
            }
            String id = UUID.randomUUID().toString();
            Map<String, Object> appointmentData = new HashMap<>();
            appointmentData.put("description", description);
            appointmentData.put("status", "pending");
            appointmentData.put("date", "");
            appointmentData.put("time", "");

            firebaseHelper.writeData("appointment/" + id, appointmentData, new FirebaseHelper.FirebaseCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AppointmentActivity.this, "Appointment requested", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(AppointmentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnBack.setOnClickListener(v -> finish());
    }
}