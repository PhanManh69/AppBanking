package com.example.appbanking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appbanking.databinding.ActivitySignupBinding;
import com.example.appbanking.utilities.Constants;
import com.example.appbanking.utilities.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
        randomAccountNumber();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.buttonSignUp.setOnClickListener(v -> {
            if (isValidSignUpDetails() && binding.checkbox.isChecked()) {
                checkPhoneNumberAndSignUp();
            } else {
                showToast("Bạn chưa đồng ý với điều khoản của chúng tôi");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void randomAccountNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000000) + 100000000;

        binding.inputAccountNumber.setText(String.valueOf(randomNumber));
    }

    private void signUp() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, Objects.requireNonNull(binding.inputName.getText()).toString());
        user.put(Constants.KEY_ACCOUNT_NUMBER, Objects.requireNonNull(binding.inputAccountNumber.getText()).toString());
        user.put(Constants.KEY_PASSWORD, Objects.requireNonNull(binding.inputPassword.getText()).toString());
        user.put(Constants.KEY_DATE, Objects.requireNonNull(binding.inputDate.getText()).toString());
        user.put(Constants.KEY_CCCD, Objects.requireNonNull(binding.inputCCCD.getText()).toString());
        user.put(Constants.KEY_PHONE, Objects.requireNonNull(binding.inputPhone.getText()).toString());
        user.put(Constants.KEY_EMAIL, Objects.requireNonNull(binding.inputEmail.getText()).toString());
        user.put(Constants.KEY_IMAGE_PROFILE, Constants.KEY_IMAGE_PROFILE);
        user.put(Constants.KEY_AMOUNT_MONEY, "0");
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, false);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME, binding.inputName.getText().toString());
                    preferenceManager.putString(Constants.KEY_PHONE, binding.inputPhone.getText().toString());
                    preferenceManager.putString(Constants.KEY_ACCOUNT_NUMBER, binding.inputAccountNumber.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
                    preferenceManager.putString(Constants.KEY_CCCD, binding.inputCCCD.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE_PROFILE, Constants.KEY_IMAGE_PROFILE);
                    preferenceManager.putString(Constants.KEY_ACCOUNT_NUMBER, "0");

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private Boolean isValidSignUpDetails() {
        if (Objects.requireNonNull(binding.inputName.getText()).toString().trim().isEmpty()) {
            showToast("Hãy nhập họ tên");
            return false;
        } else if (Objects.requireNonNull(binding.inputPassword.getText()).toString().trim().isEmpty()) {
            showToast("Hãy nhập mật khẩu");
            return false;
        } else if (Objects.requireNonNull(binding.inputConfirmPassword.getText()).toString().trim().isEmpty()) {
            showToast("Hãy nhập lại mật khẩu của bạn");
            return false;
        } else if (!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())) {
            showToast("Mật khẩu không trùng khớp");
            return false;
        } else if (Objects.requireNonNull(binding.inputDate.getText()).toString().trim().isEmpty()) {
            showToast("Hãy nhập ngày sinh");
            return false;
        } else if (Objects.requireNonNull(binding.inputCCCD.getText()).toString().trim().length() != 12) {
            showToast("Hãy nhập số CCCD hợp lệ");
            return false;
        } else if (Objects.requireNonNull(binding.inputCCCD.getText()).toString().trim().isEmpty()) {
            showToast("Hãy nhập số CCCD");
            return false;
        } else if (Objects.requireNonNull(binding.inputPhone.getText()).toString().trim().isEmpty()) {
            showToast("Hãy nhập số điện thoại");
            return false;
        } else if (binding.inputPhone.getText().toString().trim().length() != 10) {
            showToast("Hãy nhập số điện thoại hợp lệ");
            return false;
        } else if (Objects.requireNonNull(binding.inputEmail.getText()).toString().trim().isEmpty()) {
            showToast("Hãy nhập email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Hãy nhập email hợp lệ");
            return false;
        } else {
            return true;
        }
    }

    public interface OnPhoneNumberCheckComplete {
        void onPhoneNumberCheckComplete(boolean exists);
    }

    private void isPhoneNumberExists(String phoneNumber, OnPhoneNumberCheckComplete callback) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = database.collection(Constants.KEY_COLLECTION_USERS);

        Query query = usersCollection.whereEqualTo(Constants.KEY_PHONE, phoneNumber);

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = !task.getResult().isEmpty();
                        callback.onPhoneNumberCheckComplete(exists);
                    } else {
                        callback.onPhoneNumberCheckComplete(false);
                    }
                });
    }

    private void checkPhoneNumberAndSignUp() {
        String phoneNumber = Objects.requireNonNull(binding.inputPhone.getText()).toString();

        isPhoneNumberExists(phoneNumber, exists -> {
            if (!exists) {
                signUp();
            } else {
                showToast("Số điện thoại đã được sử dụng");
            }
        });
    }

    private void loading (Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}