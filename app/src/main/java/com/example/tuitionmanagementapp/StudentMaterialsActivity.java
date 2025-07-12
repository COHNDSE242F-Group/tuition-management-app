package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.*;

public class StudentMaterialsActivity extends AppCompatActivity {

    LinearLayout layoutMaterials;
    FirebaseHelper firebaseHelper;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_materials);

        userId = getIntent().getStringExtra("userId");
        if (userId == null || userId.trim().isEmpty()) {
            Toast.makeText(this, "No userId provided in Intent", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        layoutMaterials = findViewById(R.id.layoutMaterials);
        firebaseHelper = new FirebaseHelper();

        loadStudentClasses();
    }

    private void loadStudentClasses() {
        firebaseHelper.readData("student_class", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                List<String> classIds = new ArrayList<>();
                for (DataSnapshot studentClassSnap : snapshot.getChildren()) {
                    String classId = studentClassSnap.child("class").getValue(String.class);
                    if (classId == null) continue;

                    if (studentClassSnap.child("students").hasChild(userId)) {
                        classIds.add(classId);
                    }
                }

                if (classIds.isEmpty()) {
                    Toast.makeText(StudentMaterialsActivity.this, "No classes found for student", Toast.LENGTH_SHORT).show();
                } else {
                    loadMaterialsForClasses(classIds);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(StudentMaterialsActivity.this, "Failed to load classes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMaterialsForClasses(List<String> classIds) {
        layoutMaterials.removeAllViews();

        firebaseHelper.readData("materials", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                for (DataSnapshot classSnap : snapshot.getChildren()) {
                    String classId = classSnap.getKey();
                    if (!classIds.contains(classId)) continue;

                    for (DataSnapshot materialSnap : classSnap.getChildren()) {
                        String fileName = materialSnap.child("fileName").getValue(String.class);
                        String fileUrl = materialSnap.child("fileUrl").getValue(String.class);
                        Long uploadedAt = materialSnap.child("uploadedAt").getValue(Long.class);

                        String uploadedDate = uploadedAt != null
                                ? new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(uploadedAt))
                                : "Unknown";

                        if (fileName != null && fileUrl != null) {
                            addMaterialCard(fileName, fileUrl, classId, uploadedDate);
                        }
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(StudentMaterialsActivity.this, "Failed to load materials: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMaterialCard(String title, String fileUrl, String classId, String uploadedDate) {
        CardView card = new CardView(this);
        card.setCardElevation(6);
        card.setRadius(16);
        card.setUseCompatPadding(true);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16);
        card.setLayoutParams(cardParams);

        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(24, 24, 24, 24);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("📄 " + title);
        tvTitle.setTextSize(18f);
        tvTitle.setTextColor(getResources().getColor(android.R.color.black));

        TextView tvDate = new TextView(this);
        tvDate.setText("🕒 Uploaded: " + uploadedDate);
        tvDate.setTextSize(14f);
        tvDate.setTextColor(getResources().getColor(android.R.color.darker_gray));

        TextView tvClass = new TextView(this);
        tvClass.setText("🏫 Class: " + classId);
        tvClass.setTextSize(14f);
        tvClass.setTextColor(getResources().getColor(android.R.color.black));

        TextView tvView = new TextView(this);
        tvView.setText("👉 View Material");
        tvView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        tvView.setTextSize(16f);
        tvView.setPadding(0, 8, 0, 0);
        tvView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
            startActivity(intent);
        });

        innerLayout.addView(tvTitle);
        innerLayout.addView(tvDate);
        innerLayout.addView(tvClass);
        innerLayout.addView(tvView);

        card.addView(innerLayout);
        layoutMaterials.addView(card);
    }
}