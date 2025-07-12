package com.example.tuitionmanagementapp;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tuitionmanagementapp.model.Student;
import com.example.tuitionmanagementapp.model.Teacher;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

public class AdminViewAccountActivity extends AppCompatActivity {

    private static final String TAG = "AdminViewAccount";

    private LinearLayout studentContainer, teacherContainer;
    private FirebaseHelper firebaseHelper;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_accounts);

        studentContainer = findViewById(R.id.studentContainer);
        teacherContainer = findViewById(R.id.teacherContainer);

        firebaseHelper = new FirebaseHelper();

        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadStudents();
        loadTeachers();
        setupNavBar();
    }

    private void loadStudents() {
        firebaseHelper.readData("students", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                studentContainer.removeAllViews();

                if (!snapshot.exists()) {
                    Toast.makeText(AdminViewAccountActivity.this, "No students found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Student student = snap.getValue(Student.class);
                    if (student != null) {
                        String studentId = snap.getKey();  // or student.getStudentId() if you have it
                        View studentView = createUserCard(
                                studentId,
                                student.getFirstname() + " " + student.getLastname(),
                                student.getEmail(),
                                student.getContactNo(),
                                student.getHomeaddress(),
                                String.valueOf(student.getAge()),
                                student.getGender(),
                                student.getGuardianName(),
                                student.getGuardianContact(),
                                "#E0F7FA"
                        );
                        studentContainer.addView(studentView);
                    }
                }

            }


            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminViewAccountActivity.this, "Failed to load students: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error loading students", e);
            }
        });
    }

    private void loadTeachers() {
        firebaseHelper.readData("teachers", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                teacherContainer.removeAllViews();

                if (!snapshot.exists()) {
                    Toast.makeText(AdminViewAccountActivity.this, "No teachers found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Teacher teacher = snap.getValue(Teacher.class);
                    if (teacher != null) {
                        Log.d(TAG, "Teacher loaded: " + teacher.getFirstName() + " " + teacher.getLastName());
                        View teacherView = createTeacherCard(
                                teacher.getFirstName() + " " + teacher.getLastName(),
                                teacher.getEmail(),
                                teacher.getContactNo(),
                                teacher.getHomeaddress(),
                                String.valueOf(teacher.getAge()),
                                teacher.getSubject() != null ? teacher.getSubject() : "N/A",
                                teacher.getTeacherId(),

                                "#FFF3E0"
                        );

                        teacherContainer.addView(teacherView);
                    } else {
                        Log.w(TAG, "Teacher data is null for key: " + snap.getKey());
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminViewAccountActivity.this, "Failed to load teachers: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error loading teachers", e);
            }
        });
    }

    private View createUserCard(String studentId, String name, String email, String contact, String address,
                                String age, String gender, String guardianName, String guardianContact, String bgColor) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 24, 32, 24);
        layout.setBackgroundColor(Color.parseColor(bgColor));
        layout.setElevation(6);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 20);
        layout.setLayoutParams(params);

        // Add TextViews for student info as before
        layout.addView(createTextView("Name: " + name));
        layout.addView(createTextView("Email: " + email));
        layout.addView(createTextView("Contact: " + contact));
        layout.addView(createTextView("Address: " + address));
        layout.addView(createTextView("Age: " + age));
        layout.addView(createTextView("Gender: " + gender));
        layout.addView(createTextView("Guardian: " + guardianName));
        layout.addView(createTextView("Guardian Contact: " + guardianContact));

        // Buttons Layout
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 20, 0, 0);

        // Edit Button
        TextView btnEdit = new TextView(this);
        btnEdit.setText("Edit");
        btnEdit.setPadding(20, 10, 20, 10);
        btnEdit.setTextColor(Color.WHITE);
        btnEdit.setBackgroundColor(Color.parseColor("#4CAF50"));
        btnEdit.setOnClickListener(v -> {
            showEditStudentDialog(studentId, name, email, contact, address, age, gender, guardianName, guardianContact);
        });

        // Delete Button
        TextView btnDelete = new TextView(this);
        btnDelete.setText("Delete");
        btnDelete.setPadding(20, 10, 20, 10);
        btnDelete.setTextColor(Color.WHITE);
        btnDelete.setBackgroundColor(Color.parseColor("#F44336"));
        btnDelete.setOnClickListener(v -> {
            firebaseHelper.deleteData("students", studentId, new FirebaseHelper.FirebaseDeleteCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AdminViewAccountActivity.this, "Student deleted", Toast.LENGTH_SHORT).show();
                    loadStudents(); // Refresh the list
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(AdminViewAccountActivity.this, "Error deleting student", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Add buttons to layout
        buttonLayout.addView(btnEdit);
        buttonLayout.addView(btnDelete);
        layout.addView(buttonLayout);

        return layout;
    }
    private void showEditStudentDialog(String studentId, String name, String email, String contact,
                                       String address, String age, String gender, String guardianName, String guardianContact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Student");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        EditText inputName = new EditText(this);
        inputName.setHint("Name");
        inputName.setText(name);
        layout.addView(inputName);

        EditText inputEmail = new EditText(this);
        inputEmail.setHint("Email");
        inputEmail.setText(email);
        layout.addView(inputEmail);

        EditText inputContact = new EditText(this);
        inputContact.setHint("Contact");
        inputContact.setText(contact);
        layout.addView(inputContact);

        EditText inputAddress = new EditText(this);
        inputAddress.setHint("Address");
        inputAddress.setText(address);
        layout.addView(inputAddress);

        EditText inputAge = new EditText(this);
        inputAge.setHint("Age");
        inputAge.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputAge.setText(age);
        layout.addView(inputAge);

        EditText inputGender = new EditText(this);
        inputGender.setHint("Gender");
        inputGender.setText(gender);
        layout.addView(inputGender);

        EditText inputGuardianName = new EditText(this);
        inputGuardianName.setHint("Guardian Name");
        inputGuardianName.setText(guardianName);
        layout.addView(inputGuardianName);

        EditText inputGuardianContact = new EditText(this);
        inputGuardianContact.setHint("Guardian Contact");
        inputGuardianContact.setText(guardianContact);
        layout.addView(inputGuardianContact);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            Map<String, Object> updatedData = new HashMap<>();

            // Split full name into first and last name (if needed)
            String fullName = inputName.getText().toString().trim();
            String[] nameParts = fullName.split(" ");
            updatedData.put("firstname", nameParts[0]);
            updatedData.put("lastname", nameParts.length > 1 ? nameParts[1] : "");

            updatedData.put("email", inputEmail.getText().toString());
            updatedData.put("contactNo", inputContact.getText().toString());
            updatedData.put("homeaddress", inputAddress.getText().toString());
            updatedData.put("age", Integer.parseInt(inputAge.getText().toString()));
            updatedData.put("gender", inputGender.getText().toString().trim());
            updatedData.put("guardianName", inputGuardianName.getText().toString().trim());
            updatedData.put("guardianContact", inputGuardianContact.getText().toString().trim());


            firebaseHelper.updateData("students", studentId, updatedData, new FirebaseHelper.FirebaseUpdateCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AdminViewAccountActivity.this, "Student updated successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Student update successful");

                    loadStudents(); // Refresh list
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Update failed: ", e);

                    Toast.makeText(AdminViewAccountActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    private View createTeacherCard(String name, String email, String contact, String address,
                                   String age, String subject, String teacherId, String bgColor) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 24, 32, 24);
        layout.setBackgroundColor(Color.parseColor(bgColor));
        layout.setElevation(6);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 20);
        layout.setLayoutParams(params);

        // Add text views
        layout.addView(createTextView("Name: " + name));
        layout.addView(createTextView("Email: " + email));
        layout.addView(createTextView("Contact: " + contact));
        layout.addView(createTextView("Address: " + address));
        layout.addView(createTextView("Age: " + age));
        layout.addView(createTextView("Subject: " + subject));

        // Buttons Layout
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 20, 0, 0);

        // Edit Button
        TextView btnEdit = new TextView(this);
        btnEdit.setText("Edit");
        btnEdit.setPadding(20, 10, 20, 10);
        btnEdit.setTextColor(Color.WHITE);
        btnEdit.setBackgroundColor(Color.parseColor("#4CAF50"));
        btnEdit.setOnClickListener(v -> {
            showEditTeacherDialog(teacherId, name, email, contact, address, age, subject);
        });


        // Delete Button
        TextView btnDelete = new TextView(this);
        btnDelete.setText("Delete");
        btnDelete.setPadding(20, 10, 20, 10);
        btnDelete.setTextColor(Color.WHITE);
        btnDelete.setBackgroundColor(Color.parseColor("#F44336"));
        btnDelete.setOnClickListener(v -> {
            firebaseHelper.deleteData("teachers", teacherId, new FirebaseHelper.FirebaseDeleteCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AdminViewAccountActivity.this, "Teacher deleted", Toast.LENGTH_SHORT).show();
                    loadTeachers(); // Refresh the list
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(AdminViewAccountActivity.this, "Error deleting teacher", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Add buttons to layout
        buttonLayout.addView(btnEdit);
        buttonLayout.addView(btnDelete);
        layout.addView(buttonLayout);

        return layout;
    }
    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setTextColor(Color.DKGRAY);
        return textView;
    }
    private void showEditTeacherDialog(String teacherId, String name, String email, String contact,
                                       String address, String age, String subject) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Teacher");

        // Create layout for dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        EditText inputName = new EditText(this);
        inputName.setHint("Name");
        inputName.setText(name);
        layout.addView(inputName);

        EditText inputEmail = new EditText(this);
        inputEmail.setHint("Email");
        inputEmail.setText(email);
        layout.addView(inputEmail);

        EditText inputContact = new EditText(this);
        inputContact.setHint("Contact");
        inputContact.setText(contact);
        layout.addView(inputContact);

        EditText inputAddress = new EditText(this);
        inputAddress.setHint("Address");
        inputAddress.setText(address);
        layout.addView(inputAddress);

        EditText inputAge = new EditText(this);
        inputAge.setHint("Age");
        inputAge.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputAge.setText(age);
        layout.addView(inputAge);

        EditText inputSubject = new EditText(this);
        inputSubject.setHint("Subject");
        inputSubject.setText(subject);
        layout.addView(inputSubject);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {

            Toast.makeText(this, "New Name: " + inputName.getText().toString(), Toast.LENGTH_SHORT).show();


            Map<String, Object> updatedData = new HashMap<>();
            String fullName = inputName.getText().toString().trim();
            String[] nameParts = fullName.split(" ");
            updatedData.put("firstName", nameParts[0]);
            updatedData.put("lastName", nameParts.length > 1 ? nameParts[1] : "");
            updatedData.put("email", inputEmail.getText().toString());
            updatedData.put("contactNo", inputContact.getText().toString());
            updatedData.put("homeaddress", inputAddress.getText().toString());
            updatedData.put("age", Integer.parseInt(inputAge.getText().toString()));
            updatedData.put("subject", inputSubject.getText().toString());

            firebaseHelper.updateData("teachers", teacherId, updatedData, new FirebaseHelper.FirebaseUpdateCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AdminViewAccountActivity.this, "Teacher updated successfully", Toast.LENGTH_SHORT).show();

                    loadTeachers(); // Refresh list
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(AdminViewAccountActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    public void setupNavBar() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navStudent = findViewById(R.id.navStudent);
        LinearLayout navAccounts=findViewById(R.id.navAccounts);
        LinearLayout navProfile=findViewById(R.id.navProfile);


        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminHomeActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });


        navStudent.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminAssignStudentActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });

        navAccounts.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminViewAccountActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminViewProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });




    }
}
