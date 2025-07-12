package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClassCardAdapter extends RecyclerView.Adapter<ClassCardAdapter.ViewHolder> {

    private List<ClassCard> classCardList;

    public ClassCardAdapter(List<ClassCard> classCardList) {
        this.classCardList = classCardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your existing class_card layout (with 160dp width fixed)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassCard card = classCardList.get(position);
        holder.txtGrade.setText(card.getGrade());
        holder.txtDate.setText(card.getDate());
        holder.txtTime.setText(card.getTime());
        holder.txtDuration.setText(card.getDuration());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ClassDetailsActivity.class);
            intent.putExtra("classId", card.getClassId()); // Make sure ClassCard has getClassId()
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return classCardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtGrade, txtDate, txtTime, txtDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGrade = itemView.findViewById(R.id.txtGrade);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtDuration = itemView.findViewById(R.id.txtDuration);
        }
    }
}
