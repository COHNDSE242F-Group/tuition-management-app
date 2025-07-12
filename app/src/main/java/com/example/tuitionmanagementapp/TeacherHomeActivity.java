package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tuitionmanagementapp.model.Classes;
import com.example.tuitionmanagementapp.model.DayModel;
import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TeacherHomeActivity extends AppCompatActivity {

    FirebaseHelper firebaseHelper;
    private RecyclerView calendarRecyclerView;
    private DayAdapter dayAdapter;
    private RecyclerView scheduleRecyclerView;
    private ClassScheduleAdapter scheduleAdapter;
    private List<List<DayModel>> currentMonthWeeks;
    private int currentWeekIndex = 0;
    private Button btnPrevWeek;
    private Button btnNextWeek;
    private String teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        Intent intent = getIntent();
        teacherId = intent.getStringExtra("userId");

        firebaseHelper = new FirebaseHelper();

        // Setup month selector RecyclerView
        RecyclerView monthRecyclerView = findViewById(R.id.monthRecyclerView);
        monthRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<String> monthNames = Arrays.asList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        MonthAdapter monthAdapter = new MonthAdapter(monthNames, currentYear, (year, monthIndex) -> {
            currentMonthWeeks = getWeeksForMonth(year, monthIndex);
            if (!currentMonthWeeks.isEmpty()) {
                currentWeekIndex = 0;
                List<DayModel> firstWeek = currentMonthWeeks.get(0);
                dayAdapter.updateData(firstWeek);

                // Find first non-null day and fetch classes for it
                for (DayModel day : firstWeek) {
                    if (day != null) {
                        fetchClassesForDate(day.getFullDate());
                        break;
                    }
                }
            }
            updateWeekButtons();  // Update navigation buttons state after month selection
        });
        monthRecyclerView.setAdapter(monthAdapter);

        // Setup week days RecyclerView
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize dayAdapter with current week days
        List<DayModel> currentWeek = getCurrentWeek();
        dayAdapter = new DayAdapter(currentWeek, selectedDate -> fetchClassesForDate(selectedDate));
        calendarRecyclerView.setAdapter(dayAdapter);

        // Setup schedule RecyclerView
        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> timeSlots = generateTimeSlots(); // e.g. 08:00 to 20:00
        scheduleAdapter = new ClassScheduleAdapter(timeSlots, new ArrayList<>());
        scheduleRecyclerView.setAdapter(scheduleAdapter);

        // Initially fetch classes for the first day of the current week
        if (!currentWeek.isEmpty()) {
            fetchClassesForDate(currentWeek.get(0).getFullDate());
        }

        // Initialize week navigation buttons
        btnPrevWeek = findViewById(R.id.btnPrevWeek);
        btnNextWeek = findViewById(R.id.btnNextWeek);

        btnPrevWeek.setOnClickListener(v -> {
            if (currentMonthWeeks == null || currentMonthWeeks.isEmpty()) return;

            if (currentWeekIndex > 0) {
                currentWeekIndex--;
                List<DayModel> prevWeek = currentMonthWeeks.get(currentWeekIndex);
                dayAdapter.updateData(prevWeek);
                for (DayModel day : prevWeek) {
                    if (day != null) {
                        fetchClassesForDate(day.getFullDate());
                        break;
                    }
                }
                updateWeekButtons();
            }
        });

        btnNextWeek.setOnClickListener(v -> {
            if (currentMonthWeeks == null || currentMonthWeeks.isEmpty()) return;

            if (currentWeekIndex < currentMonthWeeks.size() - 1) {
                currentWeekIndex++;
                List<DayModel> nextWeek = currentMonthWeeks.get(currentWeekIndex);
                dayAdapter.updateData(nextWeek);
                for (DayModel day : nextWeek) {
                    if (day != null) {
                        fetchClassesForDate(day.getFullDate());
                        break;
                    }
                }
                updateWeekButtons();
            }
        });

        updateWeekButtons();  // Set initial button states

        horizontalScrollLoad(); // Your existing card scroll effect method
    }

    public void horizontalScrollLoad() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCards);

        // Horizontal layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Add spacing between cards
        int spacingInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacingInPixels));

        // Scroll effect
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int paddingStart = recyclerView.getPaddingStart();
                int width = recyclerView.getWidth();
                int paddingEnd = recyclerView.getPaddingEnd();

                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View child = recyclerView.getChildAt(i);

                    int childLeft = child.getLeft();
                    int childRight = child.getRight();

                    boolean fullyVisible = (childLeft >= paddingStart) && (childRight <= (width - paddingEnd));

                    if (fullyVisible) {
                        child.setScaleX(1f);
                        child.setScaleY(1f);
                        child.setAlpha(1f);
                        child.setElevation(12f);
                    } else {
                        child.setScaleX(0.85f);
                        child.setScaleY(0.85f);
                        child.setAlpha(0.5f);
                        child.setElevation(4f);
                    }
                }
            }
        });

        // Load data from Firebase
        firebaseHelper.readData("classes", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot classSnapshot) {
                List<Classes> teacherClasses = new ArrayList<>();
                for (DataSnapshot classSnap : classSnapshot.getChildren()) {
                    Classes cls = classSnap.getValue(Classes.class);
                    if (cls != null && teacherId.equals(cls.getTeacherId())) {
                        teacherClasses.add(cls);
                    }
                }

                if (teacherClasses.isEmpty()) {
                    recyclerView.setAdapter(new ClassCardAdapter(new ArrayList<>()));
                    return;
                }

                firebaseHelper.readData("schedule", new FirebaseHelper.FirebaseReadCallback() {
                    @Override
                    public void onData(DataSnapshot scheduleSnapshot) {
                        List<ClassCard> cardList = new ArrayList<>();
                        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(Calendar.getInstance().getTime());

                        for (Classes cls : teacherClasses) {
                            String classId = cls.getClassId();
                            String date = "None";
                            String time = "None";
                            String duration = "None";

                            for (DataSnapshot schedSnap : scheduleSnapshot.getChildren()) {
                                String schedClassId = schedSnap.child("classId").getValue(String.class);
                                String schedDate = schedSnap.child("date").getValue(String.class);
                                String schedTime = schedSnap.child("startTime").getValue(String.class);
                                Double schedDuration = schedSnap.child("duration").getValue(Double.class);

                                if (schedClassId != null && schedClassId.equals(classId)
                                        && schedDate != null && schedDate.compareTo(today) >= 0) {
                                    date = schedDate;
                                    time = (schedTime != null) ? formatTime(schedTime) : "None";
                                    duration = (schedDuration != null) ? schedDuration + " Hours" : "None";
                                    break;
                                }
                            }

                            cardList.add(new ClassCard(classId, "Grade " + cls.getGrade(), date, time, duration));
                        }

                        recyclerView.setAdapter(new ClassCardAdapter(cardList));
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(TeacherHomeActivity.this, "Error loading schedule", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(TeacherHomeActivity.this, "Error loading classes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ItemDecoration class to add horizontal spacing
    public static class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int horizontalSpaceWidth;

        public HorizontalSpaceItemDecoration(int horizontalSpaceWidth) {
            this.horizontalSpaceWidth = horizontalSpaceWidth;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.right = horizontalSpaceWidth;
        }
    }

    private List<DayModel> getCurrentWeek() {
        List<DayModel> week = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.US);
        SimpleDateFormat weekDayFormat = new SimpleDateFormat("EEE", Locale.US);
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        for (int i = 0; i < 7; i++) {
            week.add(new DayModel(
                    dayFormat.format(calendar.getTime()),
                    weekDayFormat.format(calendar.getTime()),
                    fullDateFormat.format(calendar.getTime()),
                    false
            ));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return week;
    }

    private void fetchClassesForDate(String selectedDate) {
        firebaseHelper.readData("schedule", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot scheduleSnapshot) {
                List<Classes> finalList = new ArrayList<>();
                Set<String> classDates = new HashSet<>();

                List<DataSnapshot> allSchedules = new ArrayList<>();
                for (DataSnapshot schedSnap : scheduleSnapshot.getChildren()) {
                    allSchedules.add(schedSnap);
                }

                if (allSchedules.isEmpty()) {
                    scheduleAdapter.updateData(new ArrayList<>());
                    dayAdapter.setClassDates(new HashSet<>());
                    return;
                }

                final int totalTasks = allSchedules.size();
                final int[] completedTasks = {0};

                for (DataSnapshot schedSnap : allSchedules) {
                    String classId = schedSnap.child("classId").getValue(String.class);
                    String startTime = schedSnap.child("startTime").getValue(String.class);
                    Double duration = schedSnap.child("duration").getValue(Double.class);
                    String date = schedSnap.child("date").getValue(String.class);

                    if (classId == null || date == null) {
                        completedTasks[0]++;
                        continue;
                    }

                    firebaseHelper.readData("classes/" + classId, new FirebaseHelper.FirebaseReadCallback() {
                        @Override
                        public void onData(DataSnapshot classSnap) {
                            Classes cls = classSnap.getValue(Classes.class);
                            if (cls != null && teacherId.equals(cls.getTeacherId())) {
                                classDates.add(date);

                                if (date.equals(selectedDate)) {
                                    cls.setStartTime(startTime);
                                    cls.setDuration(duration);
                                    cls.setDate(date);
                                    finalList.add(cls);
                                }
                            }

                            completedTasks[0]++;
                            if (completedTasks[0] == totalTasks) {
                                scheduleAdapter.updateData(finalList);
                                dayAdapter.setClassDates(classDates);
                                dayAdapter.notifyDataSetChanged(); // Force redraw dots
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            completedTasks[0]++;
                            if (completedTasks[0] == totalTasks) {
                                scheduleAdapter.updateData(finalList);
                                dayAdapter.setClassDates(classDates);
                                dayAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(TeacherHomeActivity.this, "Failed to load schedule", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> generateTimeSlots() {
        List<String> slots = new ArrayList<>();
        for (int hour = 8; hour <= 20; hour++) {
            String time = String.format(Locale.getDefault(), "%02d:00", hour);
            slots.add(time);
        }
        return slots;
    }

    private List<List<DayModel>> getWeeksForMonth(int year, int monthIndex) {
        List<List<DayModel>> weeks = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthIndex);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = calendar.getFirstDayOfWeek(); // Sunday by default
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.US);
        SimpleDateFormat weekDayFormat = new SimpleDateFormat("EEE", Locale.US);
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        List<DayModel> week = new ArrayList<>();

        // Fill initial empty days
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int blanks = (dayOfWeek - firstDayOfWeek + 7) % 7;
        for (int i = 0; i < blanks; i++) {
            week.add(null);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            DayModel dayModel = new DayModel(
                    dayFormat.format(calendar.getTime()),
                    weekDayFormat.format(calendar.getTime()),
                    fullDateFormat.format(calendar.getTime()),
                    false
            );
            week.add(dayModel);

            if (week.size() == 7) {
                weeks.add(week);
                week = new ArrayList<>();
            }
        }

        // Fill trailing days
        if (!week.isEmpty()) {
            while (week.size() < 7) {
                week.add(null);
            }
            weeks.add(week);
        }

        return weeks;
    }

    private void updateWeekButtons() {
        if (currentMonthWeeks == null || currentMonthWeeks.isEmpty()) {
            btnPrevWeek.setEnabled(false);
            btnNextWeek.setEnabled(false);
            return;
        }
        btnPrevWeek.setEnabled(currentWeekIndex > 0);
        btnNextWeek.setEnabled(currentWeekIndex < currentMonthWeeks.size() - 1);
    }

    private String formatTime(String time) {
        // Convert "16:00" or "16:00:00" to "4.00 PM"
        try {
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat sdf12 = new SimpleDateFormat("h.mm a", Locale.getDefault());
            if (time.length() == 8) time = time.substring(0, 5); // Remove seconds if present
            return sdf12.format(sdf24.parse(time));
        } catch (Exception e) {
            return time; // fallback
        }
    }
}

