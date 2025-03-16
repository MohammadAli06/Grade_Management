package com.example.grade_management.models;

import java.util.Date;

public class Announcement {
    private String title;
    private String description;
    private Date timestamp;

    public Announcement() {} // Needed for Firestore

    public Announcement(String title, String description, Date timestamp) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Date getTimestamp() { return timestamp; }
}
