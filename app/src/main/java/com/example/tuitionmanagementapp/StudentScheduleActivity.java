package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
// import com.google.firebase.auth.FirebaseAuth;

public class StudentScheduleActivity extends AppCompatActivity {

    LinearLayout layoutSchedule;
    FirebaseHelper firebaseHelper;

    // String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    String userId = "S001"; // hardcoded student ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_schedule);

        layoutSchedule = findViewById(R.id.layoutSchedule);
        firebaseHelper = new FirebaseHelper();

        /*  Dummy data
        addScheduleCard("Monday", "Mathematics", "08:00 - 09:30");
        addScheduleCard("Tuesday", "English", "10:00 - 11:30");
        addScheduleCard("Wednesday", "Science", "13:00 - 14:30");
*/



    firebaseHelper.readData("schedule/" + userId, new FirebaseHelper.FirebaseReadCallback() {
    @Override
    public void onData(DataSnapshot snapshot) {
        layoutSchedule.removeAllViews();
        for (DataSnapshot child : snapshot.getChildren()) {
            String day = child.child("day").getValue(String.class);
            String subject = child.child("subject").getValue(String.class);
            String time = child.child("time").getValue(String.class);
            addScheduleCard(day, subject, time);
        }
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(StudentScheduleActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
});


    }

    private void addScheduleCard(String day, String subject, String time) {
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

        TextView tvDay = new TextView(this);
        tvDay.setText("📅 " + day);
        tvDay.setTextSize(18f);
        tvDay.setTextColor(getResources().getColor(android.R.color.black));

        TextView tvTime = new TextView(this);
        tvTime.setText("🕒 " + time);
        tvTime.setTextSize(16f);
        tvTime.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));

        TextView tvSubject = new TextView(this);
        tvSubject.setText("📖 Subject: " + subject);
        tvSubject.setTextSize(16f);
        tvSubject.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

        innerLayout.addView(tvDay);
        innerLayout.addView(tvTime);
        innerLayout.addView(tvSubject);

        card.addView(innerLayout);
        layoutSchedule.addView(card);
    }

}
