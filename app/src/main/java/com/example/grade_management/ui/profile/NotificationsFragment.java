package com.example.grade_management.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.grade_management.MainActivity;
import com.example.grade_management.R;
import com.google.firebase.auth.FirebaseAuth;

public class NotificationsFragment extends Fragment {

    private TextView tvName, tvEmail, tvRole;
    private Button btnLogout;
    private FirebaseAuth mAuth;
    private NotificationsViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Bind UI elements
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRole = view.findViewById(R.id.tvRole);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Get ViewModel instance
        profileViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        // Observe LiveData and update UI
        profileViewModel.getName().observe(getViewLifecycleOwner(), name -> {
            tvName.setText("Name: " + name); // Concatenate "Name: " with the LiveData value
        });

        profileViewModel.getEmail().observe(getViewLifecycleOwner(), email -> {
            tvEmail.setText("Email: " + email); // Concatenate "Email: " with the LiveData value
        });

        profileViewModel.getRole().observe(getViewLifecycleOwner(), role -> {
            tvRole.setText("Role: " + role); // Concatenate "Role: " with the LiveData value
        });

        // Logout button action
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        });

        return view;
    }
}
