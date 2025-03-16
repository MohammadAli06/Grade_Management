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

import java.util.Map;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> studentName = new MutableLiveData<>();
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> role = new MutableLiveData<>();
    private final MutableLiveData<String> gpa = new MutableLiveData<>("Overall GPA: Calculating...");

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
                        }
                    })
                    .addOnFailureListener(e -> studentName.setValue("Student"));
        }
    }

    private final MutableLiveData<Map<String, Object>> grades = new MutableLiveData<>();

    public LiveData<Map<String, Object>> getGrades() {
        return grades;
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

    private double convertGradeToPoints(String grade) {
        switch (grade) {
            case "A":
                return 4.0;
            case "B":
                return 3.0;
            case "C":
                return 2.0;
            case "D":
                return 1.0;
            default:
                return 0.0;
        }
    }
}