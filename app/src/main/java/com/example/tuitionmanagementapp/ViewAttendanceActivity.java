package com.example.tuitionmanagementapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class ViewAttendanceActivity extends AppCompatActivity {

    private AppCompatButton btnPickMonth;
    private RecyclerView attendanceRecyclerView;
    private AttendanceAdapter attendanceAdapter;
    private FirebaseHelper firebaseHelper;
    private String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        btnPickMonth = findViewById(R.id.btnPickMonth);
        attendanceRecyclerView = findViewById(R.id.attendanceRecyclerView);
        attendanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseHelper = new FirebaseHelper();
        classId = getIntent().getStringExtra("classId");

        // Load current month data initially
        Calendar calendar = Calendar.getInstance();
        String currentMonth = String.format(Locale.getDefault(), "%04d-%02d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        btnPickMonth.setText(currentMonth);
        loadAttendanceData(classId, currentMonth);

        btnPickMonth.setOnClickListener(v -> openMonthPicker());
    }

    private void buildAttendanceTable(List<String> studentIds, Map<String, Set<String>> attendance, String monthKey) {
        // Create a set of all dates that have attendance records
        Set<String> datesWithAttendance = new TreeSet<>();
        for (Set<String> dates : attendance.values()) {
            datesWithAttendance.addAll(dates);
        }

        // Create header row
        LinearLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.removeAllViews();

        // Add student ID header
        TextView idHeader = createHeaderCell("Student ID");
        headerLayout.addView(idHeader);

        // Add date headers (only for days with attendance)
        for (String date : datesWithAttendance) {
            String formattedDate = formatDateForDisplay(date);
            TextView dateHeader = createHeaderCell(formattedDate);
            headerLayout.addView(dateHeader);
        }

        attendanceAdapter = new AttendanceAdapter(
                this,
                studentIds,
                attendance,
                new ArrayList<>(datesWithAttendance)
        );
        attendanceRecyclerView.setAdapter(attendanceAdapter);
    }

    private void openMonthPicker() {
        YearMonthPickerDialogFragment dialog = new YearMonthPickerDialogFragment((year, month) -> {
            String monthKey = String.format(Locale.getDefault(), "%04d-%02d", year, month);
            btnPickMonth.setText(monthKey);
            loadAttendanceData(classId, monthKey);
        });
        dialog.show(getSupportFragmentManager(), "MonthYearPicker");
    }

    private void loadAttendanceData(String classId, String monthKey) {
        firebaseHelper.readData("student_class", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                List<String> studentIds = new ArrayList<>();

                for (DataSnapshot scSnap : snapshot.getChildren()) {
                    String cls = scSnap.child("class").getValue(String.class);
                    if (classId.equals(cls)) {
                        DataSnapshot studentsSnap = scSnap.child("students");
                        for (DataSnapshot student : studentsSnap.getChildren()) {
                            studentIds.add(student.getKey());
                        }
                        break; // only one matching entry needed
                    }
                }

                if (studentIds.isEmpty()) {
                    buildAttendanceTable(Collections.emptyList(), Collections.emptyMap(), monthKey);
                    return;
                }

                // Step 2: Load attendance data
                firebaseHelper.readData("attendance", new FirebaseHelper.FirebaseReadCallback() {
                    @Override
                    public void onData(DataSnapshot attendanceSnap) {
                        Map<String, Set<String>> attendanceMap = new HashMap<>();

                        for (DataSnapshot recordSnap : attendanceSnap.getChildren()) {
                            String cls = recordSnap.child("classId").getValue(String.class);
                            String date = recordSnap.child("date").getValue(String.class);

                            if (classId.equals(cls) && date != null && date.startsWith(monthKey)) {
                                DataSnapshot studentsSnap = recordSnap.child("students");
                                for (DataSnapshot s : studentsSnap.getChildren()) {
                                    String sid = s.getKey();
                                    attendanceMap.computeIfAbsent(sid, k -> new HashSet<>()).add(date);
                                }
                            }
                        }

                        buildAttendanceTable(studentIds, attendanceMap, monthKey);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private TextView createHeaderCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(0xFFFFFFFF);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(16, 8, 16, 8);
        tv.setBackgroundColor(0xFF3a6073);

        // Fixed width for cells
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) (80 * getResources().getDisplayMetrics().density),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(params);

        return tv;
    }

    private TextView createBodyCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(0xFF333333);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(12, 6, 12, 6);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setBackgroundResource(R.drawable.cell_border); // add this drawable
        return tv;
    }

    private String formatDateForDisplay(String date) {
        try {
            String[] parts = date.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, day);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM", Locale.getDefault());
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            return date; // fallback to original format if parsing fails
        }
    }
}