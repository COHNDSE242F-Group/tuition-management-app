package com.example.tuitionmanagementapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private final Context context;
    private final List<String> studentIds;
    private final Map<String, Set<String>> attendanceMap;
    private final List<String> dateHeaders;

    public AttendanceAdapter(Context context, List<String> studentIds,
                             Map<String, Set<String>> attendanceMap,
                             List<String> dateHeaders) {
        this.context = context;
        this.studentIds = studentIds;
        this.attendanceMap = attendanceMap != null ? attendanceMap : Collections.emptyMap();
        this.dateHeaders = dateHeaders != null ? dateHeaders : new ArrayList<>();
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return new AttendanceViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        String studentId = studentIds.get(position);
        holder.row.removeAllViews();

        // Student ID cell - match header width
        TextView idCell = createTextCell(studentId, true);
        idCell.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(100), // Match header width
                ViewGroup.LayoutParams.WRAP_CONTENT));
        holder.row.addView(idCell);

        // Attendance cells - match header width
        for (String date : dateHeaders) {
            boolean present = attendanceMap.getOrDefault(studentId, Collections.emptySet()).contains(date);
            TextView attendanceCell = createTextCell(present ? "✓" : "✗", false);
            attendanceCell.setLayoutParams(new LinearLayout.LayoutParams(
                    dpToPx(60), // Match header width
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            holder.row.addView(attendanceCell);
        }
    }

    @Override
    public int getItemCount() {
        return studentIds.size();
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        LinearLayout row;

        AttendanceViewHolder(@NonNull LinearLayout itemView) {
            super(itemView);
            row = itemView;
        }
    }

    private TextView createTextCell(String text, boolean isHeader) {
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));

        if (isHeader) {
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setTextColor(0xFF333333); // Dark gray for student IDs
            tv.setBackgroundColor(0xFFEEEEEE); // Light gray background
        } else {
            // Color coded attendance
            if ("✓".equals(text)) {
                tv.setTextColor(0xFF4CAF50); // Green for present
            } else {
                tv.setTextColor(0xFFF44336); // Red for absent
            }
            tv.setBackgroundResource(R.drawable.cell_border); // Add border
        }
        return tv;
    }

    private int dpToPx(int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public void updateData(List<String> newStudentIds,
                           Map<String, Set<String>> newAttendanceMap,
                           List<String> newDateHeaders) {
        this.studentIds.clear();
        this.studentIds.addAll(newStudentIds);

        this.attendanceMap.clear();
        this.attendanceMap.putAll(newAttendanceMap);

        this.dateHeaders.clear();
        this.dateHeaders.addAll(newDateHeaders);

        notifyDataSetChanged();
    }
}