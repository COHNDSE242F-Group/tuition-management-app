package com.example.tuitionmanagementapp.model;

public class Student {
    private String studentId;
    private String firstname;
    private String lastname;
    private String homeaddress;
    private String contactNo;
    private String email;

    // Required empty constructor for Firebase
    public Student() {}

    public Student(String studentId, String firstname, String lastname, String homeaddress, String contactNo, String email) {
        this.studentId = studentId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.homeaddress = homeaddress;
        this.contactNo = contactNo;
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getHomeaddress() {
        return homeaddress;
    }

    public void setHomeaddress(String homeaddress) {
        this.homeaddress = homeaddress;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
