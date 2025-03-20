package com.example.grade_management.ui.home;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> studentName = new MutableLiveData<>();
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> role = new MutableLiveData<>();
    private final MutableLiveData<String> gpa = new MutableLiveData<>("Overall GPA: Calculating...");
    private final MutableLiveData<Map<String, Object>> grades = new MutableLiveData<>();
    private final MutableLiveData<List<Course>> enrolledCourses = new MutableLiveData<>();

    public HomeViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loadStudentName();
        loadUserProfile();
    }

    private void loadStudentName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            studentName.setValue(name);

                            // Fetch enrolled courses
                            List<String> courseIds = (List<String>) documentSnapshot.get("enrolledCourses");
                            if (courseIds != null) {
                                fetchCourseDetails(courseIds);
                            }
                        }
                    })
                    .addOnFailureListener(e -> studentName.setValue("Student"));
        }
    }

    public LiveData<String> getStudentName() {
        return studentName;
    }

    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getRole() {
        return role;
    }

    public LiveData<String> getGpa() {
        return gpa;
    }

    public LiveData<Map<String, Object>> getGrades() {
        return grades;
    }

    public LiveData<List<Course>> getEnrolledCourses() {
        return enrolledCourses;
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Fetch the user document from Firestore using the authenticated user's UID
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Update user data
                            updateUserData(documentSnapshot);

                            // Fetch grades using the studentRef field
                            fetchGrades(user.getUid());
                        }
                    })
                    .addOnFailureListener(e -> {
                        name.setValue("Error loading name");
                        role.setValue("Error loading role");
                        gpa.setValue("Overall GPA: Error");
                    });
        }
    }

    private void updateUserData(DocumentSnapshot documentSnapshot) {
        name.setValue(documentSnapshot.getString("name"));
        role.setValue(documentSnapshot.getString("role"));
    }

    private void fetchGrades(String userId) {
        db.collection("grades")
                .whereEqualTo("studentRef", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> gradesData = document.getData();
                            Log.d(TAG, "Grades data: " + gradesData); // Log the grades data
                            grades.setValue(gradesData);
                            calculateGPA(gradesData);
                        }
                    } else {
                        gpa.setValue("Overall GPA: Error fetching grades");
                        Log.e(TAG, "Error fetching grades", task.getException());
                    }
                });
    }

    private void calculateGPA(Map<String, Object> grades) {
        if (grades == null || grades.isEmpty()) {
            gpa.setValue("Overall GPA: N/A");
            return;
        }

        double totalPoints = 0;
        int totalCourses = 0;

        for (Map.Entry<String, Object> entry : grades.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Skip non-grade fields
            if (key.equals("studentRef") || key.equals("teacherID")) {
                continue;
            }

            // Ensure the value is a number
            if (value instanceof String) {
                try {
                    double score = Double.parseDouble((String) value);
                    totalPoints += score;
                    totalCourses++;
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid grade value for " + key + ": " + value);
                }
            } else if (value instanceof Number) {
                double score = ((Number) value).doubleValue();
                totalPoints += score;
                totalCourses++;
            }
        }

        if (totalCourses == 0) {
            gpa.setValue("Overall GPA: N/A");
            return;
        }

        double averageScore = totalPoints / totalCourses;
        gpa.setValue("Overall GPA: " + String.format("%.2f", averageScore));
    }

    private void fetchCourseDetails(List<String> courseIds) {
        List<Course> courses = new ArrayList<>();
        for (String courseId : courseIds) {
            db.collection("courses").document(courseId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Fetch course details
                            String courseName = documentSnapshot.getString("courseName");
                            String description = documentSnapshot.getString("description");
                            String teacher = documentSnapshot.getString("teacher");
                            int credits = documentSnapshot.getLong("credits") != null ? documentSnapshot.getLong("credits").intValue() : 0;
                            // Handle null or missing teacher field
                            if (teacher == null) {
                                teacher = "Unknown Teacher";
                            }

                            // Create Course object
                            Course course = new Course(courseName, description, teacher, credits);
                            courses.add(course);

                            // Update LiveData
                            enrolledCourses.setValue(courses);
                        } else {
                            Log.e(TAG, "Course document does not exist: " + courseId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching course: " + e.getMessage());
                    });
        }
    }

    // Course model class
    public static class Course {
        private String courseName;
        private String description;
        private String teacher;
        private int credits;

        public Course(String courseName, String description, String teacher, int credits) {
            this.courseName = courseName;
            this.description = description;
            this.teacher = teacher;
            this.credits = credits;
        }

        public String getCourseName() {
            return courseName;
        }

        public String getDescription() {
            return description;
        }

        public String getTeacher() {
            return teacher;
        }

        public int getCredits() {
            return credits;
        }
    }
}