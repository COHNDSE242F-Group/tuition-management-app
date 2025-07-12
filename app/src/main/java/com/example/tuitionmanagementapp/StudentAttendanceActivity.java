package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
 //import com.google.firebase.auth.FirebaseAuth;

public class StudentAttendanceActivity extends AppCompatActivity {

    LinearLayout layoutAttendance;
    FirebaseHelper firebaseHelper;
    String userId = "S001"; // hardcoded student id
    // String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        layoutAttendance = findViewById(R.id.layoutAttendance);
        firebaseHelper = new FirebaseHelper();

        //  Firebase part: read data from attendance/
        firebaseHelper.readData("attendance/" + userId, new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                layoutAttendance.removeAllViews(); // clear old cards
                for (DataSnapshot child : snapshot.getChildren()) {
                    String date = child.child("date").getValue(String.class);
                    String status = child.child("status").getValue(String.class);
                    addAttendanceCard(date, status);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(StudentAttendanceActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        /*
        // Dummy data part
        addAttendanceCard("2024-07-01", "Present");
        addAttendanceCard("2024-07-02", "Absent");
        addAttendanceCard("2024-07-03", "Present");
        */
    }

    private void addAttendanceCard(String date, String status) {
        CardView card = new CardView(this);
        card.setCardElevation(6);
        card.setRadius(16);
        card.setUseCompatPadding(true);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16); // spacing
        card.setLayoutParams(cardParams);

        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(24, 24, 24, 24);

        TextView tvDate = new TextView(this);
        tvDate.setText("📅 Date: " + date);
        tvDate.setTextSize(16f);
        tvDate.setTextColor(getResources().getColor(android.R.color.black));

        TextView tvStatus = new TextView(this);
        tvStatus.setText("✅ Status: " + status);
        tvStatus.setTextSize(16f);
        tvStatus.setTextColor(status.equalsIgnoreCase("Present") ?
                getResources().getColor(android.R.color.holo_green_dark) :
                getResources().getColor(android.R.color.holo_red_dark));
        tvStatus.setGravity(Gravity.END);

        innerLayout.addView(tvDate);
        innerLayout.addView(tvStatus);

        card.addView(innerLayout);
        layoutAttendance.addView(card);
    }
}
