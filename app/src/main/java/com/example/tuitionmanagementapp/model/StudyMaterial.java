package com.example.tuitionmanagementapp;

public class StudyMaterial {
    public String fileName;
    public String fileUrl;
    public String uploadedDate;

    public StudyMaterial(String fileName, String fileUrl, String uploadedDate) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.uploadedDate = uploadedDate;
    }
}