package com.example.tuitionmanagementapp;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
// import com.google.firebase.auth.FirebaseAuth;

public class StudentAssignmentsActivity extends AppCompatActivity {

    LinearLayout layoutAssignments;
    FirebaseHelper firebaseHelper;
    String userId = "S001"; // hardcoded student id

    // String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    FirebaseStorage storage;

    ActivityResultLauncher<Intent> filePickerLauncher;

    String uploadingAssignmentId; //  which assignment student is uploading for

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assignments);

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

        /*Dummy data part
        addAssignmentCard("A001", "Math Homework", "Solve page 12-14", "https://example.com/sample.pdf");
        addAssignmentCard("A002", "English Essay", "Write 200 words", "");
*/

        // Firebase part
        firebaseHelper.readData("assignments", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                layoutAssignments.removeAllViews();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String assignmentId = child.getKey();
                    String title = child.child("title").getValue(String.class);
                    String desc = child.child("description").getValue(String.class);
                    String url = child.child("url").getValue(String.class);
                    addAssignmentCard(assignmentId, title, desc, url);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(StudentAssignmentsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addAssignmentCard(String assignmentId, String title, String description, String fileUrl) {
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

        innerLayout.addView(tvTitle);
        innerLayout.addView(tvDesc);

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
