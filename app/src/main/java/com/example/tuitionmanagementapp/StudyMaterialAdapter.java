package com.example.tuitionmanagementapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tuitionmanagementapp.StudyMaterial;

import java.util.List;

public class StudyMaterialAdapter extends RecyclerView.Adapter<StudyMaterialAdapter.ViewHolder> {

    public interface OnViewClickedListener {
        void onViewClicked(String fileUrl);
    }

    private final List<StudyMaterial> items;
    private final OnViewClickedListener listener;

    public StudyMaterialAdapter(List<StudyMaterial> items, OnViewClickedListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvView;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvFileName);
            tvDate = view.findViewById(R.id.tvUploadedDate);
            tvView = view.findViewById(R.id.tvViewLink);
        }
    }

    @NonNull
    @Override
    public StudyMaterialAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_material, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudyMaterialAdapter.ViewHolder holder, int position) {
        StudyMaterial mat = items.get(position);
        holder.tvName.setText(mat.fileName);
        holder.tvDate.setText("Uploaded: " + mat.uploadedDate);
        holder.tvView.setOnClickListener(v -> listener.onViewClicked(mat.fileUrl));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}