package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectClassForExamMarksActivity extends AppCompatActivity {

    private LinearLayout layoutExamList;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_class_exam_marks);

        layoutExamList = findViewById(R.id.layoutExamList);
        firebaseHelper = new FirebaseHelper();

        loadExams();
    }

    private void loadExams() {
        firebaseHelper.readData("exams", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                for (DataSnapshot examSnap : snapshot.getChildren()) {
                    String examId = examSnap.child("examId").getValue(String.class);
                    String examName = examSnap.child("examName").getValue(String.class);
                    String classId = examSnap.child("classId").getValue(String.class);
                    String date = examSnap.child("date").getValue(String.class);

                    DataSnapshot marksSnap = examSnap.child("marks");

                    List<String> studentMarkList = new ArrayList<>();
                    for (DataSnapshot markEntry : marksSnap.getChildren()) {
                        String studentId = markEntry.getKey();
                        Long mark = markEntry.getValue(Long.class);
                        studentMarkList.add(studentId + " - " + mark + " marks");
                    }

                    addExamCard(examId, examName, classId, date, studentMarkList);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(SelectClassForExamMarksActivity.this, "Failed to load exams", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addExamCard(String examId, String examName, String classId, String date, List<String> marksList) {
        LinearLayout cardLayout = new LinearLayout(this);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setPadding(30, 30, 30, 30);
        cardLayout.setBackgroundResource(R.drawable.card_background);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 30);
        cardLayout.setLayoutParams(layoutParams);

        TextView tv = new TextView(this);
        tv.setText(examName + "\nClass: " + classId + "\nExam ID: " + examId + "\nDate: " + date);
        tv.setTextSize(16f);
        tv.setTextColor(getResources().getColor(android.R.color.black));
        cardLayout.addView(tv);

        // Spinner for marks
        Spinner marksSpinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, marksList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        marksSpinner.setAdapter(adapter);
        cardLayout.addView(marksSpinner);

        layoutExamList.addView(cardLayout);
    }
}