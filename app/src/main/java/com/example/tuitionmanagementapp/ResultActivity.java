package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ResultActivity extends AppCompatActivity {

    Spinner spinnerStudentId;
    TableLayout tableResults;
    EditText etFeedback, etFeedbackName;
    Button btnSubmitFeedback, btnBackResults;

    FirebaseHelper firebaseHelper;

    ArrayList<String> studentIdList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        spinnerStudentId = findViewById(R.id.spinnerStudentId);
        tableResults = findViewById(R.id.tableResults);
        etFeedback = findViewById(R.id.etFeedback);
        etFeedbackName = findViewById(R.id.etFeedbackName);
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);
        btnBackResults = findViewById(R.id.btnBackResults);

        firebaseHelper = new FirebaseHelper();

        loadStudentIds();

        spinnerStudentId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedId = studentIdList.get(position);
                loadExamResultsForStudent(selectedId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        btnSubmitFeedback.setOnClickListener(v -> {
            String feedback = etFeedback.getText().toString().trim();
            String name = etFeedbackName.getText().toString().trim();

            if (feedback.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Please enter both name and feedback.", Toast.LENGTH_SHORT).show();
                return;
            }

            String key = UUID.randomUUID().toString();
            Feedback newFeedback = new Feedback(feedback, name);
            firebaseHelper.writeData("feedback/" + key, newFeedback, new FirebaseHelper.FirebaseCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ResultActivity.this, "Feedback saved", Toast.LENGTH_SHORT).show();
                    etFeedback.setText("");
                    etFeedbackName.setText("");
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ResultActivity.this, "Failed to save feedback", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnBackResults.setOnClickListener(v -> finish());
    }

    private void loadStudentIds() {
        firebaseHelper.readData("exams", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                Set<String> uniqueIds = new HashSet<>();

                for (DataSnapshot examSnap : snapshot.getChildren()) {
                    DataSnapshot marksSnap = examSnap.child("marks");
                    for (DataSnapshot markEntry : marksSnap.getChildren()) {
                        uniqueIds.add(markEntry.getKey());
                    }
                }

                studentIdList.clear();
                studentIdList.addAll(uniqueIds);

                adapter = new ArrayAdapter<>(ResultActivity.this, android.R.layout.simple_spinner_item, studentIdList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStudentId.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ResultActivity.this, "Failed to load student IDs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExamResultsForStudent(String studentId) {
        tableResults.removeViews(1, Math.max(0, tableResults.getChildCount() - 1)); // clear old rows

        firebaseHelper.readData("exams", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot examSnap : snapshot.getChildren()) {
                    if (examSnap.child("marks").hasChild(studentId)) {
                        found = true;

                        String date = examSnap.child("date").getValue(String.class);
                        String examName = examSnap.child("examName").getValue(String.class);
                        String classId = examSnap.child("classId").getValue(String.class);
                        String marks = String.valueOf(examSnap.child("marks").child(studentId).getValue());

                        TableRow row = new TableRow(ResultActivity.this);
                        row.addView(createTextView(date));
                        row.addView(createTextView(examName));
                        row.addView(createTextView(classId));
                        row.addView(createTextView(marks));
                        tableResults.addView(row);
                    }
                }

                if (!found) {
                    Toast.makeText(ResultActivity.this, "No results found for " + studentId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ResultActivity.this, "Error loading results", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private TextView createTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(12, 12, 12, 12);
        return tv;
    }

    public static class Feedback {
        public String feedback;
        public String name;

        public Feedback() { }

        public Feedback(String feedback, String name) {
            this.feedback = feedback;
            this.name = name;
        }
    }
}
