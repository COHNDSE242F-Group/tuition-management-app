package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuitionmanagementapp.model.Exam;
import com.example.tuitionmanagementapp.model.StudentMark;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamMarksActivity extends AppCompatActivity {

    private Spinner spinnerExam;
    private EditText editExamName;
    private RecyclerView recyclerMarks;
    private Button btnAddNewExam, btnSave;

    private FirebaseHelper firebaseHelper;

    private String classId;  // Hardcoded for example; replace with dynamic input as needed

    private List<Exam> examList = new ArrayList<>();
    private ArrayAdapter<String> examSpinnerAdapter;

    private ExamMarksAdapter adapter;
    private Map<String, StudentMark> currentMarks = new HashMap<>(); // key: studentId, value: StudentMark
    private boolean isAddingNewExam = false;
    private boolean hasChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_marks);

        classId = getIntent().getStringExtra("classId");

        firebaseHelper = new FirebaseHelper();

        spinnerExam = findViewById(R.id.spinnerExam);
        editExamName = findViewById(R.id.editExamName);
        recyclerMarks = findViewById(R.id.recyclerMarks);
        btnAddNewExam = findViewById(R.id.btnAddNewExam);
        btnSave = findViewById(R.id.btnSave);

        recyclerMarks.setLayoutManager(new LinearLayoutManager(this));

        btnSave.setEnabled(false);

        loadExamsForClass();

        spinnerExam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (examList.isEmpty()) return;

                Exam selectedExam = examList.get(position);
                isAddingNewExam = false;
                editExamName.setEnabled(true);
                editExamName.setText(selectedExam.examName);
                loadMarksForExam(selectedExam.examId);
                hasChanges = false;
                btnSave.setEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnAddNewExam.setOnClickListener(v -> {
            isAddingNewExam = true;
            editExamName.setEnabled(true);
            editExamName.setText("");
            loadStudentsForClassOnly(); // Load students without marks
            hasChanges = false;
            btnSave.setEnabled(false);
        });

        btnSave.setOnClickListener(v -> {
            saveExamData();
        });

        editExamName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,int start,int count,int after) { }
            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count) {
                if (!isAddingNewExam) {
                    if (!examList.isEmpty()) {
                        Exam selectedExam = examList.get(spinnerExam.getSelectedItemPosition());
                        if (!s.toString().equals(selectedExam.examName)) {
                            hasChanges = true;
                            btnSave.setEnabled(true);
                        }
                    }
                } else {
                    hasChanges = !s.toString().isEmpty();
                    btnSave.setEnabled(hasChanges);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void loadExamsForClass() {
        firebaseHelper.readData("exams", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(@NonNull DataSnapshot snapshot) {
                examList.clear();
                List<String> examNames = new ArrayList<>();

                for (DataSnapshot examSnap : snapshot.getChildren()) {
                    Exam exam = examSnap.getValue(Exam.class);
                    if (exam != null && classId.equals(exam.classId)) {
                        examList.add(exam);
                        examNames.add(exam.examName);
                    }
                }

                runOnUiThread(() -> {
                    examSpinnerAdapter = new ArrayAdapter<>(ExamMarksActivity.this,
                            android.R.layout.simple_spinner_item, examNames);
                    examSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerExam.setAdapter(examSpinnerAdapter);

                    if (!examList.isEmpty()) {
                        spinnerExam.setSelection(0);
                        Exam selectedExam = examList.get(0);
                        editExamName.setText(selectedExam.examName);
                        loadMarksForExam(selectedExam.examId);
                    } else {
                        // No exams - allow adding new exam immediately or show empty
                        editExamName.setText("");
                        editExamName.setEnabled(false);
                        currentMarks.clear();
                        adapter = new ExamMarksAdapter(new ArrayList<>(), markChangedListener);
                        recyclerMarks.setAdapter(adapter);
                        btnSave.setEnabled(false);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(ExamMarksActivity.this, "Failed to load exams", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadMarksForExam(String examId) {
        firebaseHelper.readData("student_class", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(@NonNull DataSnapshot snapshot) {
                DataSnapshot targetClassStudentsSnap = null;
                for (DataSnapshot scSnap : snapshot.getChildren()) {
                    if (classId.equals(scSnap.child("class").getValue(String.class))) {
                        targetClassStudentsSnap = scSnap.child("students");
                        break;
                    }
                }

                if (targetClassStudentsSnap == null) {
                    runOnUiThread(() -> Toast.makeText(ExamMarksActivity.this, "No students found for class", Toast.LENGTH_SHORT).show());
                    return;
                }

                List<String> studentIds = new ArrayList<>();
                for (DataSnapshot studentIdSnap : targetClassStudentsSnap.getChildren()) {
                    studentIds.add(studentIdSnap.getKey());
                }

                firebaseHelper.readData("exams/" + examId + "/marks", new FirebaseHelper.FirebaseReadCallback() {
                    @Override
                    public void onData(@NonNull DataSnapshot marksSnapshot) {
                        List<StudentMark> studentMarks = new ArrayList<>();

                        for (String studentId : studentIds) {
                            firebaseHelper.readData("students/" + studentId, new FirebaseHelper.FirebaseReadCallback() {
                                @Override
                                public void onData(@NonNull DataSnapshot studentSnap) {
                                    String firstName = studentSnap.child("firstname").getValue(String.class);
                                    String lastName = studentSnap.child("lastname").getValue(String.class);
                                    String fullName = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();

                                    Integer mark = marksSnapshot.child(studentId).getValue(Integer.class);
                                    StudentMark sm = new StudentMark(studentId, fullName, mark);
                                    studentMarks.add(sm);
                                    currentMarks.put(studentId, sm);

                                    if (studentMarks.size() == studentIds.size()) {
                                        runOnUiThread(() -> {
                                            adapter = new ExamMarksAdapter(studentMarks, markChangedListener);
                                            recyclerMarks.setAdapter(adapter);
                                            btnSave.setEnabled(false);
                                        });
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    Toast.makeText(ExamMarksActivity.this, "Error loading student: " + studentId, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> Toast.makeText(ExamMarksActivity.this, "Failed to load marks", Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(ExamMarksActivity.this, "Failed to load students", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadStudentsForClassOnly() {
        firebaseHelper.readData("student_class", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(@NonNull DataSnapshot snapshot) {
                DataSnapshot targetClassStudentsSnap = null;
                for (DataSnapshot scSnap : snapshot.getChildren()) {
                    if (classId.equals(scSnap.child("class").getValue(String.class))) {
                        targetClassStudentsSnap = scSnap.child("students");
                        break;
                    }
                }

                if (targetClassStudentsSnap == null) {
                    runOnUiThread(() -> Toast.makeText(ExamMarksActivity.this, "No students found for class", Toast.LENGTH_SHORT).show());
                    return;
                }

                List<String> studentIds = new ArrayList<>();
                for (DataSnapshot studentIdSnap : targetClassStudentsSnap.getChildren()) {
                    studentIds.add(studentIdSnap.getKey());
                }

                List<StudentMark> studentMarks = new ArrayList<>();

                for (String studentId : studentIds) {
                    firebaseHelper.readData("students/" + studentId, new FirebaseHelper.FirebaseReadCallback() {
                        @Override
                        public void onData(@NonNull DataSnapshot studentSnap) {
                            String firstName = studentSnap.child("firstname").getValue(String.class);
                            String lastName = studentSnap.child("lastname").getValue(String.class);
                            String fullName = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();

                            StudentMark sm = new StudentMark(studentId, fullName, null);
                            studentMarks.add(sm);
                            currentMarks.put(studentId, sm);

                            if (studentMarks.size() == studentIds.size()) {
                                runOnUiThread(() -> {
                                    adapter = new ExamMarksAdapter(studentMarks, markChangedListener);
                                    recyclerMarks.setAdapter(adapter);
                                    btnSave.setEnabled(false);
                                });
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(ExamMarksActivity.this, "Error loading student: " + studentId, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(ExamMarksActivity.this, "Failed to load students", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private final ExamMarksAdapter.MarkChangedListener markChangedListener = new ExamMarksAdapter.MarkChangedListener() {
        @Override
        public void onMarkChanged(String studentId, Integer newMark) {
            StudentMark sm = currentMarks.get(studentId);
            if (sm != null) {
                sm.mark = newMark;
                hasChanges = true;
                btnSave.setEnabled(true);
            }
        }
    };

    private void saveExamData() {
        String examName = editExamName.getText().toString().trim();
        if (examName.isEmpty()) {
            Toast.makeText(this, "Exam name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);

        if (isAddingNewExam) {
            String newExamId = generateNextExamId();

            Exam newExam = new Exam();
            newExam.examId = newExamId;
            newExam.classId = classId;
            newExam.examName = examName;// Add current date if needed

            Map<String, Integer> marksMap = new HashMap<>();
            for (StudentMark sm : currentMarks.values()) {
                if (sm.mark != null) {
                    marksMap.put(sm.studentId, sm.mark);
                }
            }
            newExam.marks = marksMap;

            firebaseHelper.writeData("exams/" + newExamId, newExam, new FirebaseHelper.FirebaseCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(ExamMarksActivity.this, "New exam saved", Toast.LENGTH_SHORT).show();
                        isAddingNewExam = false;
                        loadExamsForClass();
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(ExamMarksActivity.this, "Failed to save exam", Toast.LENGTH_SHORT).show();
                        btnSave.setEnabled(true);
                    });
                }
            });

        } else {
            int pos = spinnerExam.getSelectedItemPosition();
            if (pos < 0 || pos >= examList.size()) return;

            Exam selectedExam = examList.get(pos);
            selectedExam.examName = examName;

            Map<String, Integer> marksMap = new HashMap<>();
            for (StudentMark sm : currentMarks.values()) {
                if (sm.mark != null) {
                    marksMap.put(sm.studentId, sm.mark);
                }
            }
            selectedExam.marks = marksMap;

            firebaseHelper.writeData("exams/" + selectedExam.examId, selectedExam, new FirebaseHelper.FirebaseCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(ExamMarksActivity.this, "Exam updated", Toast.LENGTH_SHORT).show();
                        hasChanges = false;
                        btnSave.setEnabled(false);
                        loadExamsForClass();
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(ExamMarksActivity.this, "Failed to update exam", Toast.LENGTH_SHORT).show();
                        btnSave.setEnabled(true);
                    });
                }
            });
        }
    }

    private String generateNextExamId() {
        int max = 0;
        for (Exam exam : examList) {
            try {
                int num = Integer.parseInt(exam.examId.replaceAll("[^0-9]", ""));
                if (num > max) max = num;
            } catch (Exception ignored) {}
        }
        return String.format("E%03d", max + 1);
    }
}