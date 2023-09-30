package com.example.appbanking.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appbanking.databinding.ActivityQrcodeBinding;
import com.example.appbanking.utilities.Constants;
import com.example.appbanking.utilities.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class QRCodeActivity extends AppCompatActivity {
    private ActivityQrcodeBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        binding.image.setOnClickListener(v -> {
            scanCode();
        });

        showQRCode();
        loadUserDetails();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            checkNumberAccount(result.getContents());
        }
    });

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.textNumberAccount.setText(preferenceManager.getString(Constants.KEY_ACCOUNT_NUMBER));
    }

    private void showQRCode() {
        String textToEncode = preferenceManager.getString(Constants.KEY_ACCOUNT_NUMBER);

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(textToEncode, BarcodeFormat.QR_CODE, 400, 400);

            binding.qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void isAccountNumberExists(String accountNumber, TransferMoneyActivity.OnNumberAccountCheckComplete callback) {
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

    private void checkNumberAccount(String accountNumber) {
        isAccountNumberExists(accountNumber, exists -> {
            if (!exists) {
                showToast("Tài khoản thụ hưởng không tồn tại!");
            } else {
                Intent intent = new Intent(getApplicationContext(), InfoTransferActivity.class);
                startActivity(intent);
            }
        });
    }
}