package com.example.grade_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.grade_management.ui.attendance.DashboardFragment;
import com.example.grade_management.ui.home.HomeFragment;
import com.example.grade_management.ui.profile.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class StudentActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    DashboardFragment notificationFragment = new DashboardFragment();
    NotificationsFragment settingsFragment = new NotificationsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        bottomNavigationView  = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                    return true;
                } else if (itemId == R.id.navigation_dashboard) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationFragment).commit();
                    return true;
                } else if (itemId == R.id.navigation_notifications) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, settingsFragment).commit();
                    return true;
                }
                return false;
            }
        });

    }
}