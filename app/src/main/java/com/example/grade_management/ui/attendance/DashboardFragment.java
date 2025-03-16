package com.example.grade_management.ui.attendance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grade_management.adapters.AttendanceAdapter;
import com.example.grade_management.AttendanceItem;
import com.example.grade_management.R;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private ProgressBar progressAttendance;
    private TextView tvAttendanceSummary;
    private RecyclerView recyclerAttendance;
    private AttendanceAdapter attendanceAdapter;
    private List<AttendanceItem> attendanceList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize UI components
        tvAttendanceSummary = root.findViewById(R.id.tvAttendanceSummary);
        progressAttendance = root.findViewById(R.id.progressAttendance);
        recyclerAttendance = root.findViewById(R.id.recyclerAttendance);

        // Initialize RecyclerView
        recyclerAttendance.setLayoutManager(new LinearLayoutManager(getContext()));
        attendanceList = new ArrayList<>();

        // Sample Data for Attendance
        attendanceList.add(new AttendanceItem("Math", 80));
        attendanceList.add(new AttendanceItem("Science", 75));
        attendanceList.add(new AttendanceItem("English", 90));
        attendanceList.add(new AttendanceItem("History", 85));

        // Set Adapter
        attendanceAdapter = new AttendanceAdapter(attendanceList);
        recyclerAttendance.setAdapter(attendanceAdapter);

        // Calculate overall attendance
        updateOverallAttendance();

        return root;
    }

    private void updateOverallAttendance() {
        int totalAttendance = 0;
        for (AttendanceItem item : attendanceList) {
            totalAttendance += item.getAttendancePercentage();
        }

        int overallAttendance = totalAttendance / attendanceList.size();
        tvAttendanceSummary.setText("Attendance: " + overallAttendance + "%");
        progressAttendance.setProgress(overallAttendance);
    }
}
