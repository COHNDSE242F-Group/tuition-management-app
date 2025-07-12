package com.example.tuitionmanagementapp.model;

public class Teacher {
    private String teacherId;
    private String fname;
    private String lname;
    private int age;
    private String homeaddress;
    private String contactNo;
    private String subject;
    private String email;

    public Teacher() {}

    public Teacher(String teacherId, String fname, String lname, String homeaddress, String contactNo, String email, int age, String subject) {
        this.teacherId = teacherId;
        this.fname = fname;
        this.lname = lname;
        this.homeaddress = homeaddress;
        this.contactNo = contactNo;
        this.email = email;
        this.age = age;
        this.subject = subject;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getFirstName() {
        return fname;
    }

    public void setFirstName(String firstName) {
        this.fname = firstName;
    }

    public String getLastName() {
        return lname;
    }

    public void setLastName(String lastName) {
        this.lname = lastName;
    }

    public String getHomeaddress() {
        return homeaddress;
    }

    public void setHomeaddress(String homeaddress) {
        this.homeaddress = homeaddress;
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
