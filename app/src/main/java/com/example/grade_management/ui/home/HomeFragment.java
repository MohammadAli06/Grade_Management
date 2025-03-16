package com.example.grade_management.ui.home;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.grade_management.AnnouncementActivity;
import com.example.grade_management.R;

import java.util.ArrayList;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView tvWelcome, tvGradeOverview;
    private ListView listGrades;
    private ArrayAdapter<String> gradesAdapter;
    private ArrayList<String> gradesList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        tvWelcome = root.findViewById(R.id.tvWelcome);
        tvGradeOverview = root.findViewById(R.id.tvGradeOverview);
        listGrades = root.findViewById(R.id.listGrades);

        // Set up the ListView and adapter
        gradesList = new ArrayList<>();
        gradesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, gradesList);
        listGrades.setAdapter(gradesAdapter);

        // Handle announcements icon click
        ImageView iconAnnouncements = root.findViewById(R.id.iconAnnouncements);
        iconAnnouncements.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AnnouncementActivity.class);
            startActivity(intent);
        });

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Observe LiveData
        homeViewModel.getStudentName().observe(getViewLifecycleOwner(), name -> {
            tvWelcome.setText("Welcome, " + name + "!");
        });

        homeViewModel.getGpa().observe(getViewLifecycleOwner(), gpa -> {
            tvGradeOverview.setText(gpa);
        });

        // Observe grades and update the ListView
        homeViewModel.getGrades().observe(getViewLifecycleOwner(), grades -> {
            gradesList.clear();
            if (grades != null) {
                Log.d(TAG, "Grades data: " + grades); // Log the grades data
                for (Map.Entry<String, Object> entry : grades.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    // Skip non-grade fields
                    if (!key.equals("studentRef") && !key.equals("teacherID")) {
                        gradesList.add(key + ": " + value);
                    }
                }
            } else {
                Log.e(TAG, "Grades data is null");
            }
            gradesAdapter.notifyDataSetChanged();
        });

        return root;
    }
}