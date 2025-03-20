package com.example.grade_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.grade_management.ui.thome.DashboardFragment;
import com.example.grade_management.ui.tadd.AddFragment;
import com.example.grade_management.ui.tnotifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class TeacherActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    DashboardFragment homeFragment = new DashboardFragment();
    AddFragment notificationFragment = new AddFragment();
    NotificationsFragment settingsFragment = new NotificationsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        bottomNavigationView  = findViewById(R.id.Tbottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.Tcontainer,homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_dashboardT) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.Tcontainer, homeFragment).commit();
                    return true;
                } else if (itemId == R.id.navigation_addT) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.Tcontainer, notificationFragment).commit();
                    return true;
                } else if (itemId == R.id.navigation_notificationsT) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.Tcontainer, settingsFragment).commit();
                    return true;
                }
                return false;
            }
        });

    }
}