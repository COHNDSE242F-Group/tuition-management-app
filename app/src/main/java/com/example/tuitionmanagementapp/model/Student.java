package com.example.tuitionmanagementapp.model;

public class Student {
    private String studentId;
    private String firstname;
    private String lastname;
    private String homeaddress;
    private String contactNo;
    private String email;
    private int age;
    private String gender;
    private String guardianName;
    private String guardianContact;
    private String password;


    public Student() {}

    public Student(String studentId, String firstname, String lastname, String homeaddress,
                   String contactNo, String email, int age, String gender, String guardianName, String guardianContact,String password) {
        this.studentId = studentId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.homeaddress = homeaddress;
        this.contactNo = contactNo;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.guardianName = guardianName;
        this.guardianContact = guardianContact;
        this.password=password;
    }

    // Getters and setters

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianContact() {
        return guardianContact;
    }

    public void setGuardianContact(String guardianContact) {
        this.guardianContact = guardianContact;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
