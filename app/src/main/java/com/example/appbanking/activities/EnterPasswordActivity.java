package com.example.appbanking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appbanking.databinding.ActivityEnterPasswordBinding;
import com.example.appbanking.utilities.Constants;
import com.example.appbanking.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class EnterPasswordActivity extends AppCompatActivity {
    private ActivityEnterPasswordBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnterPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager((getApplicationContext()));

        setListeners();
        loadUserDetails();
    }

    private void setListeners() {
        binding.textLogout.setOnClickListener(v -> signOutWithConfirmation());
        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
    }

    private String maskPhoneNumber(String phoneNumber) {
        int length = phoneNumber.length();
        if (length >= 5) {
            StringBuilder masked = new StringBuilder();
            masked.append(phoneNumber.substring(0, 3));
            for (int i = 3; i < length - 2; i++) {
                masked.append('*');
            }
            masked.append(phoneNumber.substring(length - 2));
            return masked.toString();
        } else {
            return phoneNumber;
        }
    }

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));

        String originalPhoneNumber = preferenceManager.getString(Constants.KEY_PHONE);
        String maskedPhoneNumber = maskPhoneNumber(originalPhoneNumber);
        binding.textPhone.setText(maskedPhoneNumber);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_PHONE, preferenceManager.getString(Constants.KEY_PHONE))
                .whereEqualTo(Constants.KEY_PASSWORD, Objects.requireNonNull(binding.inputPassword.getText()).toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_PHONE, documentSnapshot.getString(Constants.KEY_PHONE));
                        preferenceManager.putString(Constants.KEY_ACCOUNT_NUMBER, documentSnapshot.getString(Constants.KEY_ACCOUNT_NUMBER));
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                        preferenceManager.putString(Constants.KEY_CCCD, documentSnapshot.getString(Constants.KEY_CCCD));
                        preferenceManager.putString(Constants.KEY_DATE, documentSnapshot.getString(Constants.KEY_DATE));
                        preferenceManager.putString(Constants.KEY_PASSWORD, documentSnapshot.getString(Constants.KEY_PASSWORD));
                        preferenceManager.putString(Constants.KEY_IMAGE_PROFILE, documentSnapshot.getString(Constants.KEY_IMAGE_PROFILE));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Không thể đăng nhập");
                    }
                });
    }

    private void signOutWithConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setPositiveButton("Có", (dialog, which) -> {
                    signOut();
                })
                .setNegativeButton("Không", (dialog, which) -> {

                })
                .show();
    }

    private  Boolean isValidSignInDetails() {
        if (Objects.requireNonNull(binding.inputPassword.getText()).toString().trim().isEmpty()) {
            showToast("Hãy nhập mật khẩu");
            return false;
        } else {
            return true;
        }
    }

    private void signOut() {
        showToast("Đăng xuất ...");
        preferenceManager.clear();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}