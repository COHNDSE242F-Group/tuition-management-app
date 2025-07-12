package com.example.tuitionmanagementapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";

    // Put your database URL here once
    private static final String DATABASE_URL = "https://tuition-management-syste-a31c0-default-rtdb.asia-southeast1.firebasedatabase.app";

    private final FirebaseDatabase database;

    // Constructor initializes FirebaseDatabase with the fixed URL
    public FirebaseHelper() {
        database = FirebaseDatabase.getInstance(DATABASE_URL);
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void writeData(String path, Object value, final FirebaseCallback callback) {
        DatabaseReference ref = database.getReference(path);
        ref.setValue(value)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Data written to " + path);
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to write data to " + path, e);
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void readData(String path, final FirebaseReadCallback callback) {
        DatabaseReference ref = database.getReference(path);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Data read from " + path);
                if (callback != null) callback.onData(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read data from " + path, error.toException());
                if (callback != null) callback.onError(error.toException());
            }
        });
    }public void deleteData(String path, String key, FirebaseDeleteCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path).child(key);
        ref.removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    public void updateData(String path, String key, Map<String, Object> updatedData, FirebaseUpdateCallback callback) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path).child(key);
        ref.updateChildren(updatedData)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    public interface FirebaseUpdateCallback {
        void onSuccess();
        void onError(Exception e);
    }



    public interface FirebaseDeleteCallback {
        void onSuccess();
        void onError(Exception e);
    }


    public interface FirebaseCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface FirebaseReadCallback {
        void onData(DataSnapshot snapshot);
        void onError(Exception e);
    }
}