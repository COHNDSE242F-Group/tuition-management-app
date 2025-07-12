package com.example.tuitionmanagementapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuitionmanagementapp.model.Assignment;

import java.util.List;
import java.util.Locale;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    private final List<Assignment> assignments;
    private final Context context;

    public AssignmentAdapter(Context context, List<Assignment> assignments) {
        this.context = context;
        this.assignments = assignments;
    }

    @NonNull
    @Override
    public AssignmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentAdapter.ViewHolder holder, int position) {
        Assignment assignment = assignments.get(position);

        holder.tvFileName.setText(assignment.fileName);

        String date = DateFormat.format("dd MMM yyyy hh:mm a", assignment.uploadedAt).toString();
        holder.tvUploadDate.setText("Uploaded: " + date);

        holder.tvUploadedBy.setText("By: " + assignment.uploadedBy);

        holder.itemView.setOnClickListener(v -> {
            if (assignment.fileUrl != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(assignment.fileUrl));
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Cannot open this file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No URL found for this assignment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName, tvUploadDate, tvUploadedBy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvUploadDate = itemView.findViewById(R.id.tvUploadDate);
            tvUploadedBy = itemView.findViewById(R.id.tvUploadedBy);
        }
    }
}