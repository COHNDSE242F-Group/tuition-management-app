package com.example.tuitionmanagementapp.model;

public class DayModel {
    private String day;         // "12"
    private String weekday;     // "Fri"
    private String fullDate;
    private boolean hasClass;// "2025-07-12"

    public DayModel() {}

    public DayModel(String day, String weekday, String fullDate, boolean hasClass) {
        this.day = day;
        this.weekday = weekday;
        this.fullDate = fullDate;
        this.hasClass = hasClass;
    }

    public String getDay() { return day; }
    public String getWeekday() { return weekday; }
    public String getFullDate() { return fullDate; }
    public boolean hasClass() { return hasClass; }
    public void setHasClass(boolean hasClass) { this.hasClass = hasClass; }
}