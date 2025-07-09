package com.example.tuitionmanagementapp.model;

public class Teacher {
    private String teacherId;
    private String name;
    private int age;
    private String contactNo;
    private String subject;
    private String email;

    public Teacher() {}

    public Teacher(String teacherId, String name, int age, String contactNo, String subject, String email) {
        this.teacherId = teacherId;
        this.name = name;
        this.age = age;
        this.contactNo = contactNo;
        this.subject = subject;
        this.email = email;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
