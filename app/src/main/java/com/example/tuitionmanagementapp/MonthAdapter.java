package com.example.tuitionmanagementapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {

    private final List<String> months;
    private final int year;
    private final OnMonthSelectedListener listener;
    private int selectedPosition = -1;

    public interface OnMonthSelectedListener {
        void onMonthSelected(int year, int monthIndex);
    }

    public MonthAdapter(List<String> months, int year, OnMonthSelectedListener listener) {
        this.months = months;
        this.year = year;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_month, parent, false);
        return new MonthViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        String monthName = months.get(position);
        holder.monthText.setText(monthName);

        boolean isSelected = position == selectedPosition;

        holder.monthText.setBackgroundResource(
                isSelected ? R.drawable.bg_month_selected : R.drawable.bg_month_unselected
        );
        holder.monthText.setTextColor(
                ContextCompat.getColor(holder.itemView.getContext(),
                        isSelected ? android.R.color.white : R.color.teal_700)
        );

        holder.monthText.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_POSITION) return;

            int oldSelected = selectedPosition;
            selectedPosition = adapterPos;

            notifyItemChanged(oldSelected);
            notifyItemChanged(selectedPosition);

            listener.onMonthSelected(year, adapterPos);
        });
    }

    @Override
    public int getItemCount() {
        return months.size();
    }

    static class MonthViewHolder extends RecyclerView.ViewHolder {
        TextView monthText;

        public MonthViewHolder(@NonNull View itemView) {
            super(itemView);
            monthText = itemView.findViewById(R.id.monthNameText);
        }
    }

    public void setSelectedMonth(int monthIndex) {
        int oldSelected = selectedPosition;
        selectedPosition = monthIndex;
        notifyItemChanged(oldSelected);
        notifyItemChanged(selectedPosition);
    }

    public void setSelectedPosition(int position) {
        int oldSelected = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldSelected);
        notifyItemChanged(position);
    }
}