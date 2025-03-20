package com.example.grade_management.ui.thome;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> teacherName = new MutableLiveData<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    public DashboardViewModel() {
        loadTeacherName();

    }

    private void loadTeacherName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    teacherName.setValue(name != null ? name : "Teacher");
                } else {
                    teacherName.setValue("Teacher");
                }
            }).addOnFailureListener(e -> teacherName.setValue("Teacher"));
        } else {
            teacherName.setValue("Teacher");
        }
    }

    public LiveData<String> getTeacherName() {
        return teacherName;
    }

}