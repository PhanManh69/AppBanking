package com.example.appbanking.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appbanking.databinding.ActivityTransferContentBinding;
import com.example.appbanking.utilities.Constants;
import com.example.appbanking.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class TransferContentActivity extends AppCompatActivity {
    private ActivityTransferContentBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
        loadUserDetails();
    }

    private void setListeners() {
        binding.buttonContinue.setOnClickListener(v -> loadingData());
    }

    @SuppressLint("SetTextI18n")
    private void loadUserDetails() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        @SuppressLint("DefaultLocale") String dateTimeString = String.format("%02d-%02d-%04d %02d:%02d:%02d.", day, month, year, hour, minute, second);

        binding.transferSuccessfully2.setText("số tiền " + preferenceManager.getString(Constants.KEY_NUMBER_MONEY) + " VNĐ đến số tài");
        binding.transferSuccessfully3.setText("khoản " + preferenceManager.getString(Constants.KEY_ACCOUNT_NUMBER_BENEFICIARY) + " / " + preferenceManager.getString(Constants.KEY_NAME_BENEFICIARY));
        binding.transferSuccessfully4.setText("vào lúc " + dateTimeString);
        binding.transferSuccessfully5.setText("Nội dung: " + preferenceManager.getString(Constants.KEY_TRANSFER_CONTENT));
    }

    private void loadingData() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_PHONE, preferenceManager.getString(Constants.KEY_PHONE))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_PHONE, documentSnapshot.getString(Constants.KEY_PHONE));
                        preferenceManager.putString(Constants.KEY_ACCOUNT_NUMBER, documentSnapshot.getString(Constants.KEY_ACCOUNT_NUMBER));
                        preferenceManager.putString(Constants.KEY_AMOUNT_MONEY, documentSnapshot.getString(Constants.KEY_AMOUNT_MONEY));
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                        preferenceManager.putString(Constants.KEY_CCCD, documentSnapshot.getString(Constants.KEY_CCCD));
                        preferenceManager.putString(Constants.KEY_DATE, documentSnapshot.getString(Constants.KEY_DATE));
                        preferenceManager.putString(Constants.KEY_IMAGE_PROFILE, documentSnapshot.getString(Constants.KEY_IMAGE_PROFILE));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        startActivity(intent);
                    } else {
                        loading(false);
                    }
                });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonContinue.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonContinue.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}