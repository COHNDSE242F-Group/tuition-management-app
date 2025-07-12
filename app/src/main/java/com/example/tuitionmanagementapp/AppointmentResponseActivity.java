package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

public class AppointmentResponseActivity extends AppCompatActivity {

    LinearLayout pendingContainer;
    TableLayout tableScheduledAppointments;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_response);

        pendingContainer = findViewById(R.id.pendingContainer);
        tableScheduledAppointments = findViewById(R.id.tableScheduledAppointments);
        firebaseHelper = new FirebaseHelper();

        loadPendingAppointments();
        loadConfirmedAppointments();
    }

    private void loadPendingAppointments() {
        firebaseHelper.readData("appointment", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                pendingContainer.removeAllViews();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String id = child.getKey();
                    String status = child.child("status").getValue(String.class);
                    if ("pending".equals(status)) {
                        String desc = child.child("description").getValue(String.class);

                        View card = getLayoutInflater().inflate(R.layout.dynamic_appointment_card, null);

                        TextView tvDescription = card.findViewById(R.id.tvDescription);
                        DatePicker datePicker = card.findViewById(R.id.datePicker);
                        TimePicker timePicker = card.findViewById(R.id.timePicker);
                        Button btnSchedule = card.findViewById(R.id.btnSchedule);

                        tvDescription.setText("Description: " + desc);

                        btnSchedule.setOnClickListener(v -> {
                            int day = datePicker.getDayOfMonth();
                            int month = datePicker.getMonth() + 1;
                            int year = datePicker.getYear();
                            int hour = timePicker.getHour();
                            int minute = timePicker.getMinute();

                            String date = String.format("%04d-%02d-%02d", year, month, day);
                            String time = String.format("%02d:%02d", hour, minute);

                            Map<String, Object> update = new HashMap<>();
                            update.put("date", date);
                            update.put("time", time);
                            update.put("status", "confirmed");
                            update.put("description", desc); // ✅ keep the original description

                            firebaseHelper.writeData("appointment/" + id, update, new FirebaseHelper.FirebaseCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(AppointmentResponseActivity.this, "Appointment Scheduled", Toast.LENGTH_SHORT).show();
                                    pendingContainer.removeView(card);
                                    loadConfirmedAppointments();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(AppointmentResponseActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });

                        pendingContainer.addView(card);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AppointmentResponseActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadConfirmedAppointments() {
        tableScheduledAppointments.removeViews(1, Math.max(0, tableScheduledAppointments.getChildCount() - 1));

        firebaseHelper.readData("appointment", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String status = child.child("status").getValue(String.class);
                    if ("confirmed".equals(status)) {
                        String desc = child.child("description").getValue(String.class);
                        String date = child.child("date").getValue(String.class);
                        String time = child.child("time").getValue(String.class);

                        TableRow row = new TableRow(AppointmentResponseActivity.this);
                        TextView tv1 = new TextView(AppointmentResponseActivity.this);
                        tv1.setText(desc);
                        TextView tv2 = new TextView(AppointmentResponseActivity.this);
                        tv2.setText(date + " " + time);

                        row.addView(tv1);
                        row.addView(tv2);
                        tableScheduledAppointments.addView(row);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("Firebase", "Load confirmed failed", e);
            }
        });
    }
}
