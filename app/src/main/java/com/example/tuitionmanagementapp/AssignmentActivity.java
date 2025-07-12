package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

public class AssignmentActivity extends AppCompatActivity {

    private LinearLayout notificationLayout; // This must match the notification section's layout ID
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignments); // Your second XML

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        notificationLayout = findViewById(R.id.notificationMessagesContainer);
        // Must match LinearLayout in XML
        firebaseHelper = new FirebaseHelper();

        // Load notifications from Firebase
        firebaseHelper.readData("notification", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                notificationLayout.removeAllViews(); // clear previous
                for (DataSnapshot child : snapshot.getChildren()) {
                    String title = child.child("title").getValue(String.class);
                    String message = child.child("message").getValue(String.class);
                    String date = child.child("date").getValue(String.class);

                    TextView tv = new TextView(AssignmentActivity.this);
                    tv.setText("🔔 " + title + " - " + message + " (" + date + ")");
                    tv.setTextSize(16);
                    tv.setTextColor(getResources().getColor(android.R.color.black));
                    tv.setPadding(0, 0, 0, 20);

                    notificationLayout.addView(tv);
                }
            }

            @Override
            public void onError(Exception e) {
                TextView tv = new TextView(AssignmentActivity.this);
                tv.setText("❌ Failed to load notifications");
                notificationLayout.addView(tv);
            }
        });
    }
}
