package com.example.grade_management.ui.attendance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<List<Map<String, Object>>> attendanceRecords = new MutableLiveData<>();
    private final MutableLiveData<Float> attendancePercentage = new MutableLiveData<>(0f);

    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    public DashboardViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loadAttendanceData();
    }

    private void loadAttendanceData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("attendance")
                    .whereEqualTo("studentId", user.getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Map<String, Object>> records = new ArrayList<>();
                        for (var doc : queryDocumentSnapshots.getDocuments()) {
                            records.add(doc.getData());
                        }
                        attendanceRecords.setValue(records);
                        calculateAttendancePercentage(queryDocumentSnapshots);
                    });
        }
    }

    private void calculateAttendancePercentage(QuerySnapshot querySnapshot) {
        int totalClasses = querySnapshot.size();
        int attendedClasses = 0;
        for (var doc : querySnapshot) {
            Boolean attended = doc.getBoolean("attended");
            if (attended != null && attended) {
                attendedClasses++;
            }
        }
        float percentage = (totalClasses == 0) ? 0 : ((float) attendedClasses / totalClasses) * 100;
        attendancePercentage.setValue(percentage);
    }

    public LiveData<List<Map<String, Object>>> getAttendanceRecords() {
        return attendanceRecords;
    }

    public LiveData<Float> getAttendancePercentage() {
        return attendancePercentage;
    }
}
