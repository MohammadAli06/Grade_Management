package com.example.grade_management.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.grade_management.R;
import com.example.grade_management.models.Announcement;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {
    private List<Announcement> announcements;

    public AnnouncementAdapter(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);
        holder.title.setText(announcement.getTitle());
        holder.description.setText(announcement.getDescription());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        holder.timestamp.setText(sdf.format(announcement.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, timestamp;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            timestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}
