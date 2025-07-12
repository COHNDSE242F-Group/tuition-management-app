package com.example.tuitionmanagementapp.model;

public class Schedule {
    private String classId;
    private String date;
    private double duration;
    private String startTime;

    // Required for Firebase
    public Schedule() {}

    public Schedule(String classId, String date, double duration, String startTime) {
        this.classId = classId;
        this.date = date;
        this.duration = duration;
        this.startTime = startTime;
    }

    public String getClassId() { return classId; }
    public String getDate() { return date; }
    public double getDuration() { return duration; }
    public String getStartTime() { return startTime; }
}