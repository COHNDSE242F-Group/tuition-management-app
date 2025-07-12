package com.example.tuitionmanagementapp;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuitionmanagementapp.model.StudentMark;

import java.util.List;

public class ExamMarksAdapter extends RecyclerView.Adapter<ExamMarksAdapter.ViewHolder> {
    public interface MarkChangedListener {
        void onMarkChanged(String studentId, Integer newMark);
    }

    private final List<StudentMark> studentMarks;
    private final MarkChangedListener markChangedListener;

    public ExamMarksAdapter(List<StudentMark> studentMarks, MarkChangedListener listener) {
        this.studentMarks = studentMarks;
        this.markChangedListener = listener;
    }

    @NonNull
    @Override
    public ExamMarksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_mark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamMarksAdapter.ViewHolder holder, int position) {
        StudentMark sm = studentMarks.get(position);

        holder.txtStudentId.setText(sm.studentId);

        if (sm.studentName != null && !sm.studentName.isEmpty()) {
            holder.txtStudentName.setText(sm.studentName);
        } else {
            holder.txtStudentName.setText("Name unknown");
        }

        if (sm.mark != null) {
            holder.editMark.setText(String.valueOf(sm.mark));
        } else {
            holder.editMark.setText("");
            holder.editMark.setHint("Absent");
        }

        if (holder.editMark.getTag() instanceof TextWatcher) {
            holder.editMark.removeTextChangedListener((TextWatcher) holder.editMark.getTag());
        }

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                Integer mark = null;
                if (!text.isEmpty()) {
                    try {
                        mark = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        mark = null;
                    }
                }
                markChangedListener.onMarkChanged(sm.studentId, mark);
            }
        };

        holder.editMark.addTextChangedListener(watcher);
        holder.editMark.setTag(watcher);
    }

    @Override
    public int getItemCount() {
        return studentMarks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtStudentId, txtStudentName;
        EditText editMark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtStudentId = itemView.findViewById(R.id.txtStudentId);
            txtStudentName = itemView.findViewById(R.id.txtStudentName);
            editMark = itemView.findViewById(R.id.editMark);
        }
    }
}