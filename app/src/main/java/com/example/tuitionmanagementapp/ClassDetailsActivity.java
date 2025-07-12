package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.tuitionmanagementapp.model.Classes;
import com.example.tuitionmanagementapp.model.Schedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import android.Manifest;

public class ClassDetailsActivity extends AppCompatActivity {

    TextView tvClassGrade, tvSubject, tvNextDate, tvTime, tvDuration;
    FirebaseHelper firebaseHelper;
    String classId;
    private static final int QR_SCAN_REQUEST_CODE = 101;
    private static final int PICK_FILE_REQUEST_CODE = 100;
    private Uri fileUri;
    private String teacherId;
    private boolean isUploadingAssignment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        tvClassGrade = findViewById(R.id.tvClassGrade);
        tvSubject = findViewById(R.id.tvSubject);
        tvNextDate = findViewById(R.id.tvNextDate);
        tvTime = findViewById(R.id.tvTime);
        tvDuration = findViewById(R.id.tvDuration);

        firebaseHelper = new FirebaseHelper();
        classId = getIntent().getStringExtra("classId");

        if (classId != null) {
            fetchClassDetails(classId);
        }

        // Button actions
        findViewById(R.id.btnViewAttendance).setOnClickListener(v -> {
            Intent intent = new Intent(ClassDetailsActivity.this, ViewAttendanceActivity.class);
            intent.putExtra("classId", classId); // replace "C001" with actual classId
            startActivity(intent);
        });
        findViewById(R.id.btnMarkAttendance).setOnClickListener(v -> {
            Intent intent = new Intent(this, com.journeyapps.barcodescanner.CaptureActivity.class);
            intent.setAction("com.google.zxing.client.android.SCAN");
            startActivityForResult(intent, QR_SCAN_REQUEST_CODE);
        });
        findViewById(R.id.btnViewAssignments).setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewAssignmentsActivity.class);
            intent.putExtra("classId", classId);
            intent.putExtra("teacherId", teacherId); // Replace with dynamic teacherId if needed
            startActivity(intent);
        });
        findViewById(R.id.btnExamMarks).setOnClickListener(v -> {
            Intent intent = new Intent(ClassDetailsActivity.this, ExamMarksActivity.class);
            intent.putExtra("classId", classId); // Replace with the actual class ID
            startActivity(intent);
        });
        findViewById(R.id.btnAddAssignment).setOnClickListener(v -> {
            isUploadingAssignment = true;
            pickFile();
        });

        findViewById(R.id.btnAddStudyMaterials).setOnClickListener(v -> {
            isUploadingAssignment = false;
            pickFile();
        });
        findViewById(R.id.btnViewStudyMaterials).setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewAssignmentsActivity.class);
            intent.putExtra("classId", classId);
            intent.putExtra("teacherId", teacherId); // Replace with dynamic teacherId if needed
            startActivity(intent);
        });
    }

    private void fetchClassDetails(String classId) {
        firebaseHelper.readData("classes/" + classId, new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot classSnap) {
                Classes cls = classSnap.getValue(Classes.class);
                if (cls != null) {
                    tvClassGrade.setText("Grade: " + cls.getGrade());
                    tvSubject.setText("Subject: " + cls.getSubject());
                    teacherId = cls.getTeacherId();

                    // Now fetch next schedule (today or future)
                    fetchNextSchedule(classId);
                } else {
                    Toast.makeText(ClassDetailsActivity.this, "Class not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ClassDetailsActivity.this, "Failed to fetch class", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchNextSchedule(String classId) {
        firebaseHelper.readData("schedule", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                Schedule nextClass = null;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Schedule sched = snap.getValue(Schedule.class);
                    if (sched != null && classId.equals(sched.getClassId()) && sched.getDate().compareTo(today) >= 0) {
                        if (nextClass == null || sched.getDate().compareTo(nextClass.getDate()) < 0) {
                            nextClass = sched;
                        }
                    }
                }

                if (nextClass != null) {
                    tvNextDate.setText("Next Class: " + nextClass.getDate());
                    tvTime.setText("Time: " + nextClass.getStartTime());
                    tvDuration.setText("Duration: " + nextClass.getDuration() + " hrs");
                } else {
                    tvNextDate.setText("Next Class: None");
                    tvTime.setText("Time: -");
                    tvDuration.setText("Duration: -");
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ClassDetailsActivity.this, "Failed to fetch schedule", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == QR_SCAN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String scannedStudentId = data.getStringExtra("SCAN_RESULT");
            markAttendanceForStudent(scannedStudentId);
        } else if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(fileUri, takeFlags);
            uploadFileToFirebase(fileUri);
        }
    }

    private void markAttendanceForStudent(String studentId) {
        String todayDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());

        firebaseHelper.readData("attendance", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                boolean found = false;
                String recordKey = null;

                for (DataSnapshot record : snapshot.getChildren()) {
                    String cls = record.child("classId").getValue(String.class);
                    String date = record.child("date").getValue(String.class);

                    if (classId.equals(cls) && todayDate.equals(date)) {
                        recordKey = record.getKey();
                        found = true;
                        break;
                    }
                }

                if (found && recordKey != null) {
                    // Update existing record: add student under "students"
                    String studentPath = "attendance/" + recordKey + "/students/" + studentId;
                    firebaseHelper.writeData(studentPath, true, new FirebaseHelper.FirebaseCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(ClassDetailsActivity.this, "Attendance marked successfully!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ClassDetailsActivity.this, "Failed to mark attendance.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // Create new attendance record
                    String newKey = FirebaseDatabase.getInstance().getReference("attendance").push().getKey();

                    if (newKey == null) {
                        Log.e("MarkAttendance", "Failed to generate new key");
                        return;
                    }

                    Map<String, Object> attendanceEntry = new HashMap<>();
                    attendanceEntry.put("classId", classId);
                    attendanceEntry.put("date", todayDate);

                    Map<String, Object> students = new HashMap<>();
                    students.put(studentId, true);
                    attendanceEntry.put("students", students);

                    firebaseHelper.writeData("attendance/" + newKey, attendanceEntry, new FirebaseHelper.FirebaseCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(ClassDetailsActivity.this, "Attendance marked successfully!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ClassDetailsActivity.this, "Failed to mark attendance.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("MarkAttendance", "Failed to read attendance data", e);
            }
        });
    }

    private void uploadFileToFirebase(Uri fileUri) {
        if (fileUri == null) {
            Toast.makeText(this, "File URI is null", Toast.LENGTH_SHORT).show();
            return;
        }

        try (InputStream inputStream = getContentResolver().openInputStream(fileUri)) {
            if (inputStream == null) {
                Toast.makeText(this, "File is not accessible", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (classId == null || classId.isEmpty()) {
            Toast.makeText(this, "Class ID is missing. Cannot upload.", Toast.LENGTH_SHORT).show();
            Log.e("FirebaseUpload", "Missing classId");
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String fileName = System.currentTimeMillis() + "_" + getFileName(fileUri);

        // Use different folder based on type
        String firebaseFolder = isUploadingAssignment ? "assignments" : "materials";
        StorageReference fileRef = storageRef.child(firebaseFolder + "/" + fileName);

        String mimeType = getContentResolver().getType(fileUri);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(mimeType != null ? mimeType : "application/octet-stream")
                .build();

        UploadTask uploadTask = fileRef.putFile(fileUri, metadata);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Log.d("FirebaseUpload", "Download URL: " + downloadUrl);

                // Save metadata
                String nodePath = isUploadingAssignment ? "assignments" : "materials";
                String key = FirebaseDatabase.getInstance().getReference(nodePath).child(classId).push().getKey();

                if (key == null) {
                    Toast.makeText(this, "Failed to generate upload key", Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseUpload", "Key is null");
                    return;
                }

                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("fileName", fileName);
                dataMap.put("fileUrl", downloadUrl);
                dataMap.put("uploadedAt", System.currentTimeMillis());
                dataMap.put("uploadedBy", teacherId != null ? teacherId : "unknown");

                firebaseHelper.writeData(nodePath + "/" + classId + "/" + key, dataMap, new FirebaseHelper.FirebaseCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ClassDetailsActivity.this,
                                isUploadingAssignment ? "Assignment uploaded!" : "Study material uploaded!",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ClassDetailsActivity.this,
                                "Upload metadata failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to get file URL", Toast.LENGTH_LONG).show();
                Log.e("FirebaseUpload", "Download URL fetch failed", e);
            });
        });

        uploadTask.addOnFailureListener(e -> {
            Log.e("FirebaseUpload", "Upload failed", e);
            Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private String getFileName(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (index != -1) {
                String name = cursor.getString(index);
                cursor.close();
                return name;
            }
            cursor.close();
        }
        return "file_" + System.currentTimeMillis();
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

}