package com.example.tuitionmanagementapp;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuitionmanagementapp.model.DayModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    private List<DayModel> days;
    private Set<String> classDates = new HashSet<>(); // Dates like "2025-07-14"
    private int selectedPosition = -1;
    private final OnDateClickListener listener;

    public interface OnDateClickListener {
        void onDateClicked(String date);
    }

    public DayAdapter(List<DayModel> days, OnDateClickListener listener) {
        this.days = days;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDay, txtWeekday;
        View dot;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            txtDay = view.findViewById(R.id.txtDay);
            txtWeekday = view.findViewById(R.id.txtWeekDay);
            dot = view.findViewById(R.id.classIndicator);
            cardView = (CardView) view;
        }
    }

    @NonNull
    @Override
    public DayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayAdapter.ViewHolder holder, int position) {
        DayModel day = days.get(position);

        if (day == null) {
            holder.txtDay.setText("");
            holder.txtWeekday.setText("");
            holder.dot.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(Color.TRANSPARENT);
            holder.itemView.setOnClickListener(null);
            return;
        }

        holder.txtDay.setText(day.getDay());
        holder.txtWeekday.setText(day.getWeekday());

        // Show dot if this date has a class
        holder.dot.setVisibility(classDates.contains(day.getFullDate()) ? View.VISIBLE : View.GONE);

        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#3a7bd5"));
            holder.txtDay.setTextColor(Color.WHITE);
            holder.txtWeekday.setTextColor(Color.WHITE);
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.txtDay.setTextColor(Color.parseColor("#1A237E"));
            holder.txtWeekday.setTextColor(Color.parseColor("#607D8B"));
        }

        holder.itemView.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_POSITION) return;

            int oldSelected = selectedPosition;
            selectedPosition = adapterPos;
            notifyItemChanged(oldSelected);
            notifyItemChanged(selectedPosition);

            listener.onDateClicked(day.getFullDate());
        });
    }

    @Override
    public int getItemCount() {
        return days != null ? days.size() : 0;
    }

    public void updateData(List<DayModel> newDays) {
        this.days = newDays;
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    public void setClassDates(Set<String> classDates) {
        this.classDates = classDates != null ? classDates : new HashSet<>();
        notifyDataSetChanged();
    }

    public void setSelectedDate(String fullDate) {
        if (days == null) return;
        for (int i = 0; i < days.size(); i++) {
            DayModel day = days.get(i);
            if (day != null && fullDate.equals(day.getFullDate())) {
                int oldSelected = selectedPosition;
                selectedPosition = i;
                notifyItemChanged(oldSelected);
                notifyItemChanged(selectedPosition);
                break;
            }
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}