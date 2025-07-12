package com.example.tuitionmanagementapp.model;

public class Classes {
    private String classId;
    private int grade;
    private String subject;
    private String teacherId;
    private String date;
    private String startTime;
    private double duration;

    // Required for Firebase
    public Classes() {}

    public Classes(String classId, int grade, String subject, String teacherId) {
        this.classId = classId;
        this.grade = grade;
        this.subject = subject;
        this.teacherId = teacherId;
    }

    public String getClassId() { return classId; }
    public int getGrade() { return grade; }
    public String getSubject() { return subject; }
    public String getTeacherId() { return teacherId; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}