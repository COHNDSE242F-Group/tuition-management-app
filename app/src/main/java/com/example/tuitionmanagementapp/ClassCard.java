package com.example.tuitionmanagementapp;

public class ClassCard {
    private String classId, grade, date, time, duration;

    public ClassCard(String classId, String grade, String date, String time, String duration) {
        this.classId = classId;
        this.grade = grade;
        this.date = date;
        this.time = time;
        this.duration = duration;
    }

    public String getClassId() { return classId; }
    public String getGrade() { return grade; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getDuration() { return duration; }
}
