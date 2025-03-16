package com.example.grade_management;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grade_management.adapters.AnnouncementAdapter;
import com.example.grade_management.models.Announcement;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AnnouncementAdapter adapter;
    private List<Announcement> announcementList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        recyclerView = findViewById(R.id.recyclerAnnouncements);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        announcementList = new ArrayList<>();
        adapter = new AnnouncementAdapter(announcementList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        db.collection("announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        announcementList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Announcement announcement = document.toObject(Announcement.class);
                            announcementList.add(announcement);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Announcements", "Error fetching announcements", task.getException());
                        Toast.makeText(this, "Failed to load announcements", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
