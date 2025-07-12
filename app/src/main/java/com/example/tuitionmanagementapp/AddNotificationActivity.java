package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNotificationActivity extends AppCompatActivity {

    private EditText editTitle, editMessage;
    private Button btnSubmit, btnLogout;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notification);

        editTitle = findViewById(R.id.editTitle);
        editMessage = findViewById(R.id.editMessage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogout = findViewById(R.id.btnLogout);

        firebaseHelper = new FirebaseHelper();

        btnSubmit.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String message = editMessage.getText().toString().trim();
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            if (title.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            NotificationModel notification = new NotificationModel(title, message, date, "active");

            String id = "notification_" + System.currentTimeMillis(); // Unique key
            firebaseHelper.writeData("notification/" + id, notification, new FirebaseHelper.FirebaseCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AddNotificationActivity.this, "Notification saved", Toast.LENGTH_SHORT).show();
                    editTitle.setText("");
                    editMessage.setText("");
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(AddNotificationActivity.this, "Failed to save", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Logout → back to parent home
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AddNotificationActivity.this, ParentHomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Data Model
    public static class NotificationModel {
        public String title;
        public String message;
        public String date;
        public String status;

        public NotificationModel() {} // Required for Firebase

        public NotificationModel(String title, String message, String date, String status) {
            this.title = title;
            this.message = message;
            this.date = date;
            this.status = status;
        }
    }
}
