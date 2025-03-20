package com.example.grade_management.ui.thome;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.grade_management.R;
import com.example.grade_management.TProfileActivity;
import com.example.grade_management.databinding.FragmentDashboardBinding;
import com.example.grade_management.databinding.FragmentThomeBinding;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentThomeBinding binding;
    private TextView tvWelcome;
    private DashboardViewModel homeViewModel;


    private ListView listSchedule, listDeadlines;
    private ArrayAdapter<String> scheduleAdapter, deadlinesAdapter;
    private List<String> scheduleList, deadlinesList;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentThomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tvWelcome = root.findViewById(R.id.tvWelcome);
        homeViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Observe ViewModel to update UI
        homeViewModel.getTeacherName().observe(getViewLifecycleOwner(), name ->
                tvWelcome.setText("Welcome, " + name + "!")
        );

        ImageView iconProf = root.findViewById(R.id.iconprof);
        iconProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), TProfileActivity.class);
                startActivity(intent);
            }
        });

        listSchedule = binding.listSchedule;
        listDeadlines = binding.listDeadlines;

        // Set up the ListView and adapters
        scheduleList = new ArrayList<>();
        scheduleList.add("9:00 AM - Math Class");
        scheduleList.add("11:00 AM - Physics Class");
        scheduleAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, scheduleList);
        listSchedule.setAdapter(scheduleAdapter);

        deadlinesList = new ArrayList<>();
        deadlinesList.add("Assignment 1 - Due Tomorrow");
        deadlinesList.add("Midterm Exam - Next Week");
        deadlinesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, deadlinesList);
        listDeadlines.setAdapter(deadlinesAdapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}