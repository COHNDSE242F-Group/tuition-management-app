// FeeStatusActivity.java (Full working version)
package com.example.tuitionmanagementapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeeStatusActivity extends AppCompatActivity {

    private Spinner spinnerStudentId, spinnerMonth;
    private EditText editName, editAmount;
    private Button btnPay, btnBack;
    private TextView txtPaymentHistory;

    private FirebaseHelper firebaseHelper;
    private String selectedStudentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fee);

        firebaseHelper = new FirebaseHelper();

        spinnerStudentId = findViewById(R.id.spinnerStudentId);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        editName = findViewById(R.id.editName);
        editAmount = findViewById(R.id.editAmount);
        btnPay = findViewById(R.id.btnPay);
        btnBack = findViewById(R.id.btnBackFee);
        txtPaymentHistory = findViewById(R.id.txtPaymentHistory);

        setupMonthSpinner();
        loadStudentIds();

        btnPay.setOnClickListener(v -> handlePayment());
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupMonthSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);
    }

    private void loadStudentIds() {
        firebaseHelper.readData("students", new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                List<String> studentIds = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    studentIds.add(snap.getKey());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        FeeStatusActivity.this,
                        android.R.layout.simple_spinner_item,
                        studentIds);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStudentId.setAdapter(adapter);

                spinnerStudentId.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                        selectedStudentId = studentIds.get(position);
                        loadPaymentHistory(selectedStudentId);
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {
                        selectedStudentId = null;
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(FeeStatusActivity.this, "Error loading students", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePayment() {
        String name = editName.getText().toString().trim();
        String amount = editAmount.getText().toString().trim();
        String month = spinnerMonth.getSelectedItem().toString();

        if (selectedStudentId == null || name.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentPath = "payment/" + selectedStudentId;
        String payKey = "pay" + System.currentTimeMillis();

        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("month", month);
        paymentData.put("year", "2025"); // Optional: change if you have dynamic year
        paymentData.put("amount", amount);

        firebaseHelper.writeData(paymentPath + "/" + payKey, paymentData, new FirebaseHelper.FirebaseCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(FeeStatusActivity.this, "Payment added successfully", Toast.LENGTH_SHORT).show();
                loadPaymentHistory(selectedStudentId);
                editName.setText("");
                editAmount.setText("");
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(FeeStatusActivity.this, "Failed to save payment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPaymentHistory(String studentId) {
        firebaseHelper.readData("payment/" + studentId, new FirebaseHelper.FirebaseReadCallback() {
            @Override
            public void onData(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    txtPaymentHistory.setText("No payment history found");
                    return;
                }

                StringBuilder history = new StringBuilder("Payment History:\n\n");
                for (DataSnapshot paymentSnap : snapshot.getChildren()) {
                    String month = paymentSnap.child("month").getValue(String.class);
                    String year = paymentSnap.child("year").getValue(String.class);
                    String amount = paymentSnap.child("amount").getValue(String.class);

                    history.append("• ").append(month).append(" ").append(year)
                            .append(" - Rs.").append(amount).append("\n");
                }
                txtPaymentHistory.setText(history.toString());
            }

            @Override
            public void onError(Exception e) {
                txtPaymentHistory.setText("❌ Error loading payment history");
            }
        });
    }
}
