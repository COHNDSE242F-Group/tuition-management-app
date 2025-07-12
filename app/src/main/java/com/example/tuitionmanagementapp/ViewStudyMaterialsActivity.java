package com.example.tuitionmanagementapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewStudyMaterialsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseHelper firebaseHelper;
    String classId;
    StudyMaterialAdapter adapter;
    List<com.example.tuitionmanagementapp.StudyMaterial> materialsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_study_materials);

        recyclerView = findViewById(R.id.recyclerStudyMaterials);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseHelper = new FirebaseHelper();
        materialsList = new ArrayList<>();
        adapter = new StudyMaterialAdapter(materialsList, this::onViewClicked);
        recyclerView.setAdapter(adapter);

        classId = getIntent().getStringExtra("classId");

        if (classId == null) {
            Toast.makeText(this, "Missing classId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadStudyMaterials();
    }

    private void loadStudyMaterials() {
        firebaseHelper.readData("materials/" + classId, new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                materialsList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String fileName = snap.child("fileName").getValue(String.class);
                    String fileUrl = snap.child("fileUrl").getValue(String.class);
                    Long uploadedAt = snap.child("uploadedAt").getValue(Long.class);

                    if (fileName != null && fileUrl != null && uploadedAt != null) {
                        String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(new Date(uploadedAt));
                        materialsList.add(new com.example.tuitionmanagementapp.StudyMaterial(fileName, fileUrl, dateStr));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ViewStudyMaterialsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onViewClicked(String fileUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
        startActivity(intent);
    }
}