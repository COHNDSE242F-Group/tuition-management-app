package com.example.tuitionmanagementapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuitionmanagementapp.model.Classes;

import java.util.ArrayList;
import java.util.List;

public class ClassScheduleAdapter extends RecyclerView.Adapter<ClassScheduleAdapter.ViewHolder> {

    private List<String> timeSlots;
    private List<Classes> classes;

    public ClassScheduleAdapter(List<String> timeSlots, List<Classes> classes) {
        this.timeSlots = timeSlots;
        this.classes = new ArrayList<>();
        if (classes != null) {
            this.classes.addAll(classes);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currentSlot = timeSlots.get(position);
        holder.timeLabel.setText(currentSlot.substring(0, 5)); // HH:mm only

        // Clear previous views
        holder.eventContainer.removeAllViews();
        holder.eventContainer.setVisibility(View.VISIBLE);

        // Check if this slot should show an event or be collapsed
        boolean isStartOfEvent = false;
        boolean isCoveredByEvent = false;
        Classes currentClass = null;
        int eventDurationSlots = 0;

        for (Classes cls : classes) {
            int startIndex = getTimeSlotIndex(cls.getStartTime());
            int durationSlots = (int) Math.ceil(cls.getDuration());

            if (startIndex == position) {
                isStartOfEvent = true;
                currentClass = cls;
                eventDurationSlots = durationSlots;
                break;
            } else if (position > startIndex && position < startIndex + durationSlots) {
                isCoveredByEvent = true;
                break;
            }
        }

        // Set item height based on its role in an event
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp == null) {
            lp = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) (72 * holder.itemView.getResources().getDisplayMetrics().density)
            );
        }

        if (isStartOfEvent) {
            // This is the starting slot of an event
            int slotHeightPx = (int) (72 * holder.itemView.getResources().getDisplayMetrics().density);
            lp.height = slotHeightPx * eventDurationSlots;
            holder.itemView.setLayoutParams(lp);

            // Inflate and configure the event view
            View eventView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.item_event, holder.eventContainer, false);

            TextView title = eventView.findViewById(R.id.eventTitle);
            TextView desc = eventView.findViewById(R.id.eventDescription);

            title.setText("Grade " + currentClass.getClassId());
            desc.setText("Duration: " + currentClass.getDuration() + " hrs");

            // Set event view height to match the combined slots
            ViewGroup.LayoutParams eventParams = eventView.getLayoutParams();
            if (eventParams == null) {
                eventParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
            }
            eventParams.height = lp.height;
            eventView.setLayoutParams(eventParams);

            holder.eventContainer.addView(eventView);

        } else if (isCoveredByEvent) {
            // This slot is covered by an event but not the starting slot
            lp.height = 0; // Collapse this slot completely
            holder.itemView.setLayoutParams(lp);
            holder.itemView.setVisibility(View.GONE);
        } else {
            // Normal empty slot
            lp.height = (int) (72 * holder.itemView.getResources().getDisplayMetrics().density);
            holder.itemView.setLayoutParams(lp);
            holder.itemView.setVisibility(View.VISIBLE);
            holder.eventContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public void updateData(List<Classes> newList) {
        classes.clear();
        classes.addAll(newList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeLabel;
        FrameLayout eventContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeLabel = itemView.findViewById(R.id.timeLabel);
            eventContainer = itemView.findViewById(R.id.eventContainer);
        }
    }

    private int getTimeSlotIndex(String time) {
        for (int i = 0; i < timeSlots.size(); i++) {
            if (timeSlots.get(i).equals(time)) return i;
        }
        return -1;
    }
}