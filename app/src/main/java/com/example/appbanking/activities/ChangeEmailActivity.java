package com.example.appbanking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appbanking.databinding.ActivityChangeEmailBinding;
import com.example.appbanking.utilities.Constants;
import com.example.appbanking.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChangeEmailActivity extends AppCompatActivity {
    private ActivityChangeEmailBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.buttonSave.setOnClickListener(v -> {
            if (isValidEmailDetails()) {
                String accountNumber = preferenceManager.getString(Constants.KEY_ACCOUNT_NUMBER);
                String newEmail = Objects.requireNonNull(binding.inputEmail.getText()).toString();
                updatePassword(accountNumber, newEmail);
            }
        });
    }

    private void updatePassword(String accountNumber, String newEmail) {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_ACCOUNT_NUMBER, accountNumber)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String documentID = documentSnapshot.getId();

                        Map<String, Object> moneyBalance = new HashMap<>();
                        moneyBalance.put(Constants.KEY_EMAIL, newEmail);

                        database.collection(Constants.KEY_COLLECTION_USERS)
                                .document(documentID)
                                .update(moneyBalance);

                        Intent intent = new Intent(getApplicationContext(), EnterPasswordActivity.class);
                        intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Không tìm thấy tài khoản");
                    }
                });
    }

    private Boolean isValidEmailDetails() {
        if (Objects.requireNonNull(binding.inputEmail.getText()).toString().trim().isEmpty()) {
            showToast("Hãy nhập email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Hãy nhập email hợp lệ");
            return false;
        } else {
            return true;
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loading (Boolean isLoading) {
        if (isLoading) {
            binding.buttonSave.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSave.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}