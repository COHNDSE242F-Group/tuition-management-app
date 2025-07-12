package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

public class ViewAssignmentsActivity extends AppCompatActivity {

    private String classId, teacherId;
    private FirebaseHelper firebaseHelper;
    private LinearLayout assignmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assignments);

        classId = getIntent().getStringExtra("classId");
        teacherId = getIntent().getStringExtra("teacherId");
        firebaseHelper = new FirebaseHelper();
        assignmentContainer = findViewById(R.id.assignmentContainer);

        if (classId != null && teacherId != null) {
            loadAssignments();
        } else {
            Toast.makeText(this, "Missing data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAssignments() {
        firebaseHelper.readData("assignments", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                boolean hasAssignments = false;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String dbClassId = snap.child("classId").getValue(String.class);
                    String dbTeacherId = snap.child("teacherId").getValue(String.class);
                    String documentUrl = snap.child("document").getValue(String.class);
                    String assignmentId = snap.child("assignmentId").getValue(String.class);

                    if (dbClassId != null && dbClassId.equals(classId)
                            && dbTeacherId != null && dbTeacherId.equals(teacherId)) {

                        hasAssignments = true;

                        Button downloadBtn = new Button(ViewAssignmentsActivity.this);
                        downloadBtn.setText("Download " + assignmentId);
                        downloadBtn.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        downloadBtn.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(documentUrl));
                            startActivity(intent);
                        });

                        assignmentContainer.addView(downloadBtn);
                    }
                }

                if (!hasAssignments) {
                    Toast.makeText(ViewAssignmentsActivity.this, "No assignments found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ViewAssignmentsActivity.this, "Failed to load assignments", Toast.LENGTH_LONG).show();
            }
        });
    }
}