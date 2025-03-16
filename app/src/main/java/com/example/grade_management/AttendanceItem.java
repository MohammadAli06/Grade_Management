package com.example.grade_management;

public class AttendanceItem {
    private String subjectName;
    private int attendancePercentage;

    public AttendanceItem(String subjectName, int attendancePercentage) {
        this.subjectName = subjectName;
        this.attendancePercentage = attendancePercentage;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getAttendancePercentage() {
        return attendancePercentage;
    }
}
