package com.example.grade_management.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
public class NotificationsViewModel extends ViewModel {


    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> role = new MutableLiveData<>();
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    public NotificationsViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loadUserProfile();
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> updateUserData(documentSnapshot))
                    .addOnFailureListener(e -> {
                        name.setValue("Error loading name");
                        email.setValue("Error loading email");
                        role.setValue("Error loading role");
                    });
        }
    }

    private void updateUserData(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
            name.setValue(documentSnapshot.getString("name"));
            email.setValue(documentSnapshot.getString("email"));
            role.setValue(documentSnapshot.getString("role"));
        }
    }

    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getRole() {
        return role;
    }
}