package com.example.tuitionmanagementapp.model;

import java.util.Map;

public class Exam {
    public String examId;
    public String examName;
    public String classId;
    public Map<String, Integer> marks;

    public Exam() {
        // Required for Firebase
    }

    public Exam(String examId, String examName, String classId, Map<String, Integer> marks) {
        this.examId = examId;
        this.examName = examName;
        this.classId = classId;
        this.marks = marks;
    }
}