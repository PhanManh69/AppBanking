package com.example.appbanking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appbanking.databinding.ActivityTransferMoneyBinding;
import com.example.appbanking.utilities.Constants;
import com.example.appbanking.utilities.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class TransferMoneyActivity extends AppCompatActivity {
    private ActivityTransferMoneyBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferMoneyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.buttonContinue.setOnClickListener(v -> {
            checkNumberAccount();
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnNumberAccountCheckComplete {
        void onNumberAccountCheckComplete(boolean exists);
    }

    private void isAccountNumberExists(String accountNumber, TransferMoneyActivity.OnNumberAccountCheckComplete callback) {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = database.collection(Constants.KEY_COLLECTION_USERS);

        Query query = usersCollection.whereEqualTo(Constants.KEY_ACCOUNT_NUMBER, accountNumber);

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = !task.getResult().isEmpty();
                        callback.onNumberAccountCheckComplete(exists);
                    } else {
                        callback.onNumberAccountCheckComplete(false);
                    }
                });
    }

    private void checkNumberAccount() {
        String accountNumber = Objects.requireNonNull(binding.inputAccountNumber.getText()).toString();

        isAccountNumberExists(accountNumber, exists -> {
            if (!exists) {
                showToast("Tài khoản thụ hưởng không tồn tại!");
                loading(false);
            } else if (accountNumber.equals(preferenceManager.getString(Constants.KEY_ACCOUNT_NUMBER))) {
                showToast("Tài khoản thụ hưởng không là tài khoản của bạn!");
                loading(false);
            } else {
                Intent intent = new Intent(getApplicationContext(), InfoTransferActivity.class);
                intent.putExtra("ACCOUNT_NUMBER_BENEFICIARY", accountNumber);
                startActivity(intent);
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