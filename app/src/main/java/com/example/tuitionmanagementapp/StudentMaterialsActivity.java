package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
// import com.google.firebase.auth.FirebaseAuth;

public class StudentMaterialsActivity extends AppCompatActivity {

    LinearLayout layoutMaterials;
    FirebaseHelper firebaseHelper;

    // String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_materials);

        layoutMaterials = findViewById(R.id.layoutMaterials);
        firebaseHelper = new FirebaseHelper();

        /*  Dummy data
        addMaterialCard("Week 1 Lecture Notes", "https://example.com/week1.pdf");
        addMaterialCard("Chapter 2 Slides", "https://example.com/chapter2.pdf");
        addMaterialCard("Revision Guide", "https://example.com/revision.pdf");
*/

        //  Firebase part
        firebaseHelper.readData("materials", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                layoutMaterials.removeAllViews();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String title = child.child("title").getValue(String.class);
                    String url = child.child("url").getValue(String.class);
                    addMaterialCard(title, url);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(StudentMaterialsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addMaterialCard(String title, String fileUrl) {
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

        TextView tvTitle = new TextView(this);
        tvTitle.setText("📄 " + title);
        tvTitle.setTextSize(18f);
        tvTitle.setTextColor(getResources().getColor(android.R.color.black));

        // View Material
        TextView tvView = new TextView(this);
        tvView.setText("👉 View Material");
        tvView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        tvView.setTextSize(16f);
        tvView.setPadding(0, 8, 0, 0);
        tvView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
            startActivity(intent);
        });

        innerLayout.addView(tvTitle);
        innerLayout.addView(tvView);

        card.addView(innerLayout);
        layoutMaterials.addView(card);
    }
}
