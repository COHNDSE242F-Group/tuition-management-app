package com.example.tuitionmanagementapp.model;



public class Student {
    private String studentId;
    private String name;
    private int age;
    private String contactNo;
    private String email;

    public Student() {}

    public Student(String studentId, String name, int age, String contactNo, String email) {
        this.studentId = studentId;
        this.name = name;
        this.age = age;
        this.contactNo = contactNo;
        this.email = email;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

