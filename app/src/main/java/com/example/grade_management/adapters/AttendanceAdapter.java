package com.example.grade_management.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grade_management.AttendanceItem;
import com.example.grade_management.R;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    private List<AttendanceItem> attendanceList;

    public AttendanceAdapter(List<AttendanceItem> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceItem item = attendanceList.get(position);
        holder.tvSubjectName.setText(item.getSubjectName());
        holder.tvAttendancePercentage.setText(item.getAttendancePercentage() + "%");
        holder.progressBar.setProgress(item.getAttendancePercentage());
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvAttendancePercentage;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvAttendancePercentage = itemView.findViewById(R.id.tvAttendancePercentage);
            progressBar = itemView.findViewById(R.id.progressAttendanceBar);
        }
    }
}
