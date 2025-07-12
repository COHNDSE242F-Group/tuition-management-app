package com.example.tuitionmanagementapp.model;

public class StudentMark {
    public String studentId;
    public String studentName;
    public Integer mark;

    public StudentMark() {}

    public StudentMark(String studentId, String studentName, Integer mark) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.mark = mark;
    }
}