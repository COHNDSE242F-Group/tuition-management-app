package com.example.tuitionmanagementapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.lang.reflect.Field;
import java.util.Calendar;

public class YearMonthPickerDialogFragment extends DialogFragment {
    public interface OnDateSetListener {
        void onDateSet(int year, int month);
    }

    private final OnDateSetListener listener;

    public YearMonthPickerDialogFragment(OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, dayOfMonth) -> {
                    if (listener != null) {
                        listener.onDateSet(selectedYear, selectedMonth + 1);
                    }
                },
                year, month, 1
        );

        // Hide the day picker
        DatePicker datePicker = dialog.getDatePicker();

        // Method 1: Try to find and hide day view (works on most devices)
        try {
            int dayId = Resources.getSystem().getIdentifier("day", "id", "android");
            if (dayId != 0) {
                View dayView = datePicker.findViewById(dayId);
                if (dayView != null) {
                    dayView.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Method 2: Alternative approach for some devices
        try {
            Field[] datePickerDialogFields = dialog.getClass().getDeclaredFields();
            for (Field field : datePickerDialogFields) {
                if (field.getName().equals("mDatePicker")) {
                    field.setAccessible(true);
                    DatePicker datePicker2 = (DatePicker) field.get(dialog);
                    Field[] datePickerFields = datePicker2.getClass().getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        if ("mDayPicker".equals(datePickerField.getName()) ||
                                "mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker2);
                            if (dayPicker != null) {
                                ((View) dayPicker).setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dialog;
    }
}