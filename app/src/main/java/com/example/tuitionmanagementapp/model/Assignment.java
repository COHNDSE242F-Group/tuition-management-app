package com.example.tuitionmanagementapp.model;

public class Assignment {
    public String fileName;
    public String fileUrl;
    public long uploadedAt;
    public String uploadedBy;

    // Required empty constructor for Firebase
    public Assignment() {}

    public Assignment(String fileName, String fileUrl, long uploadedAt, String uploadedBy) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.uploadedAt = uploadedAt;
        this.uploadedBy = uploadedBy;
    }
}
