package com.example.appbanking.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appbanking.databinding.ActivityInfoAccountBinding;
import com.example.appbanking.utilities.Constants;
import com.example.appbanking.utilities.PreferenceManager;

public class InfoAccountActivity extends AppCompatActivity {
    private ActivityInfoAccountBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
        loadUserDetails();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    @SuppressLint("SetTextI18n")
    private void loadUserDetails() {
        byte[] bytes = android.util.Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE_PROFILE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageUser.setImageBitmap(bitmap);
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.textNumberAccount.setText("Số tài khoản: " + preferenceManager.getString(Constants.KEY_ACCOUNT_NUMBER));
        binding.textEmail.setText("Email: " + preferenceManager.getString(Constants.KEY_EMAIL));
        binding.textPhone.setText("Số điện thoại: " + preferenceManager.getString(Constants.KEY_PHONE));
        binding.textCCCD.setText("Số CCCD: " + preferenceManager.getString(Constants.KEY_CCCD));
        binding.textDate.setText("Ngày sinh: " + preferenceManager.getString(Constants.KEY_DATE));
    }
}