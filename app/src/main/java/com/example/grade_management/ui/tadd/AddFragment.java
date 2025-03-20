package com.example.grade_management.ui.tadd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.grade_management.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFragment extends Fragment {

    private Spinner spinnerStudents;
    private TextView tvTeacherId;
    private LinearLayout containerGrades;
    private Button btnAddGrade;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<String> studentNames = new ArrayList<>();
    private List<String> studentIds = new ArrayList<>();
    private List<String> subjects = new ArrayList<>();
    private Map<String, EditText> subjectGradeMap = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tadd, container, false);

        // Initialize Firestore and Auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        spinnerStudents = root.findViewById(R.id.spinnerStudents);
        tvTeacherId = root.findViewById(R.id.tvTeacherId);
        containerGrades = root.findViewById(R.id.containerGrades);
        btnAddGrade = root.findViewById(R.id.btnAddGrade);

        // Set teacher ID
        String teacherId = mAuth.getCurrentUser().getUid();
        tvTeacherId.setText("Teacher ID: " + teacherId);

        // Fetch students and subjects
        fetchStudents();
        fetchSubjects();

        // Handle Add Grade button click
        btnAddGrade.setOnClickListener(v -> addGrades());

        return root;
    }

    private void fetchStudents() {
        db.collection("users")
                .whereEqualTo("role", "Student")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String studentName = document.getString("name");
                        String studentId = document.getId();
                        studentNames.add(studentName);
                        studentIds.add(studentId);
                    }
                    // Populate Spinner with student names
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(), android.R.layout.simple_spinner_item, studentNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerStudents.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching students: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchSubjects() {
        db.collection("courses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String subject = document.getId();
                        subjects.add(subject);

                        // Add EditText for each subject
                        EditText etGrade = new EditText(getContext());
                        etGrade.setHint("Enter grade for " + subject);
                        etGrade.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        containerGrades.addView(etGrade);

                        // Store EditText in map
                        subjectGradeMap.put(subject, etGrade);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching subjects: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addGrades() {
        // Get selected student ID and teacher ID
        String studentId = studentIds.get(spinnerStudents.getSelectedItemPosition());
        String teacherId = mAuth.getCurrentUser().getUid();

        // Create a map to store subject grades
        Map<String, Object> subjectGrades = new HashMap<>();
        for (String subject : subjects) {
            EditText etGrade = subjectGradeMap.get(subject);
            String grade = etGrade.getText().toString().trim();
            if (!grade.isEmpty()) {
                subjectGrades.put(subject, Integer.parseInt(grade));
            }
        }

        // Check if at least one grade is entered
        if (subjectGrades.isEmpty()) {
            Toast.makeText(getContext(), "Please enter at least one grade", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the main grades map
        Map<String, Object> grades = new HashMap<>();
        grades.put("grades", subjectGrades); // Nested map for subject grades
        grades.put("studentRef", studentId);
        grades.put("teacherID", teacherId);

        // Save grades to Firestore
        db.collection("grades").document(studentId + "_" + teacherId)
                .set(grades)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Grades added successfully", Toast.LENGTH_SHORT).show();
                    clearGradeFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error adding grades: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void clearGradeFields() {
        for (EditText etGrade : subjectGradeMap.values()) {
            etGrade.setText("");
        }
    }
}