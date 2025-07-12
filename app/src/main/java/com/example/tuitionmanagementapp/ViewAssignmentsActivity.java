package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuitionmanagementapp.model.Assignment;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAssignmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList = new ArrayList<>();

    private FirebaseHelper firebaseHelper;
    private String classId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assignments);

        recyclerView = findViewById(R.id.recyclerViewAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AssignmentAdapter(this, assignmentList);
        recyclerView.setAdapter(adapter);

        firebaseHelper = new FirebaseHelper();
        classId = getIntent().getStringExtra("classId");

        if (classId != null) {
            loadAssignments(classId);
        } else {
            Toast.makeText(this, "Class ID is missing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadAssignments(String classId) {
        firebaseHelper.readData("assignments/" + classId, new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                assignmentList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Assignment assignment = snap.getValue(Assignment.class);
                    if (assignment != null) {
                        assignmentList.add(assignment);
                    }
                }
                adapter.notifyDataSetChanged();

                if (assignmentList.isEmpty()) {
                    Toast.makeText(ViewAssignmentsActivity.this, "No assignments found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("ViewAssignments", "Failed to load assignments", e);
                Toast.makeText(ViewAssignmentsActivity.this, "Failed to load assignments", Toast.LENGTH_LONG).show();
            }
        });
    }
}