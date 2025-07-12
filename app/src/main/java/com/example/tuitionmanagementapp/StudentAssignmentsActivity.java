package com.example.tuitionmanagementapp;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentAssignmentsActivity extends AppCompatActivity {

    LinearLayout layoutAssignments;
    FirebaseHelper firebaseHelper;
    String userId; // Current student user id

    FirebaseStorage storage;

    ActivityResultLauncher<Intent> filePickerLauncher;

    String uploadingAssignmentId; // Which assignment student is uploading for

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assignments);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        if (userId == null || userId.trim().isEmpty()) {
            Toast.makeText(this, "No userId provided in Intent", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        layoutAssignments = findViewById(R.id.layoutAssignments);
        firebaseHelper = new FirebaseHelper();
        storage = FirebaseStorage.getInstance();

        // File picker launcher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        uploadSubmission(fileUri, uploadingAssignmentId);
                    }
                });

        // Step 1: Load all classes student belongs to
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

                    // Check if student exists in this class's "students"
                    if (studentClassSnap.child("students").hasChild(userId)) {
                        classIds.add(classId);
                    }
                }

                if (classIds.isEmpty()) {
                    Toast.makeText(StudentAssignmentsActivity.this, "No classes found for student", Toast.LENGTH_SHORT).show();
                } else {
                    loadAssignmentsForClasses(classIds);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(StudentAssignmentsActivity.this, "Failed to load student classes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAssignmentsForClasses(List<String> classIds) {
        layoutAssignments.removeAllViews();

        // We'll load all assignments under "assignments" node and filter by classId in memory
        firebaseHelper.readData("assignments", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                for (DataSnapshot classNode : snapshot.getChildren()) {
                    String classId = classNode.getKey();
                    if (classIds.contains(classId)) {
                        // This class has assignments
                        for (DataSnapshot assignmentSnap : classNode.getChildren()) {
                            String assignmentId = assignmentSnap.getKey();
                            String fileName = assignmentSnap.child("fileName").getValue(String.class);
                            String fileUrl = assignmentSnap.child("fileUrl").getValue(String.class);
                            // You can add title/description if you store them, or use fileName as title
                            String title = fileName != null ? fileName : "Assignment " + assignmentId;
                            String description = ""; // Add description field if you want

                            addAssignmentCard(assignmentId, classId, title, description, fileUrl);
                        }
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(StudentAssignmentsActivity.this, "Failed to load assignments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addAssignmentCard(String assignmentId, String classId, String title, String description, String fileUrl) {
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
        tvTitle.setText("📚 " + title);
        tvTitle.setTextSize(18f);
        tvTitle.setTextColor(getResources().getColor(android.R.color.black));

        TextView tvDesc = new TextView(this);
        tvDesc.setText(description);
        tvDesc.setTextSize(16f);
        tvDesc.setTextColor(getResources().getColor(android.R.color.darker_gray));

        TextView tvClass = new TextView(this);
        tvClass.setText("Class ID: " + classId);
        tvClass.setTextSize(14f);
        tvClass.setTextColor(getResources().getColor(android.R.color.black));

        innerLayout.addView(tvTitle);
        innerLayout.addView(tvDesc);
        innerLayout.addView(tvClass);

        // View Assignment
        if (fileUrl != null && !fileUrl.isEmpty()) {
            TextView tvView = new TextView(this);
            tvView.setText("📄 View Assignment");
            tvView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            tvView.setTextSize(16f);
            tvView.setPadding(0, 8, 0, 0);
            tvView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                startActivity(intent);
            });
            innerLayout.addView(tvView);
        }

        // Upload Submission
        TextView tvUpload = new TextView(this);
        tvUpload.setText("⬆ Upload Submission");
        tvUpload.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        tvUpload.setTextSize(16f);
        tvUpload.setPadding(0, 8, 0, 0);
        tvUpload.setOnClickListener(v -> {
            uploadingAssignmentId = assignmentId; // remember which assignment
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*"); // any file type
            filePickerLauncher.launch(intent);
        });
        innerLayout.addView(tvUpload);

        card.addView(innerLayout);
        layoutAssignments.addView(card);
    }

    private void uploadSubmission(Uri fileUri, String assignmentId) {
        String fileName = "submissions/" + userId + "/" + assignmentId + "/" + System.currentTimeMillis();
        StorageReference fileRef = storage.getReference().child(fileName);
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String url = uri.toString();
                    // save to DB under submissions/userId/assignmentId
                    firebaseHelper.writeData("submissions/" + userId + "/" + assignmentId + "/url", url, new FirebaseHelper.FirebaseCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(StudentAssignmentsActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(StudentAssignmentsActivity.this, "DB save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}