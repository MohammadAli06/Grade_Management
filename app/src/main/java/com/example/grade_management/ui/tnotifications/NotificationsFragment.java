package com.example.grade_management.ui.tnotifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.grade_management.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    private EditText etAnnouncementTitle, etAnnouncementDescription;
    private Button btnPostAnnouncement;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tnotifications, container, false);

        // Initialize Firestore and Auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        etAnnouncementTitle = root.findViewById(R.id.etAnnouncementTitle);
        etAnnouncementDescription = root.findViewById(R.id.etAnnouncementDescription);
        btnPostAnnouncement = root.findViewById(R.id.btnPostAnnouncement);

        // Handle Post Announcement button click
        btnPostAnnouncement.setOnClickListener(v -> postAnnouncement());

        return root;
    }

    private void postAnnouncement() {
        // Get input values
        String title = etAnnouncementTitle.getText().toString().trim();
        String description = etAnnouncementDescription.getText().toString().trim();

        // Validate input
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get teacher ID
        String teacherId = mAuth.getCurrentUser().getUid();

        // Create a map to store announcement data
        Map<String, Object> announcementData = new HashMap<>();
        announcementData.put("title", title);
        announcementData.put("description", description);
        announcementData.put("teacherID", teacherId);
        announcementData.put("timestamp", com.google.firebase.Timestamp.now()); // Use Firestore Timestamp

        // Save announcement to Firestore
        db.collection("announcements").document()
                .set(announcementData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Announcement posted successfully", Toast.LENGTH_SHORT).show();
                    clearAnnouncementFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error posting announcement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearAnnouncementFields() {
        etAnnouncementTitle.setText("");
        etAnnouncementDescription.setText("");
    }
}