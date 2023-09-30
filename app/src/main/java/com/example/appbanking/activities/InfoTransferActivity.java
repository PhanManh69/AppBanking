package com.example.appbanking.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appbanking.databinding.ActivityInfoTransferBinding;
import com.example.appbanking.utilities.Constants;
import com.example.appbanking.utilities.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InfoTransferActivity extends AppCompatActivity {
    private ActivityInfoTransferBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoTransferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
        loadUserDetails();
        isAccountBeneficiary();
        decimalFormat();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));
        binding.buttonContinue.setOnClickListener(v -> {
            String accountNumber = binding.textNumberAccount.getText().toString();
            String accountNumberBeneficiary = binding.textNumberAccountBeneficiary.getText().toString();

            String moneyText = Objects.requireNonNull(binding.inputMoney.getText()).toString();
            String cleanedMoneyText = moneyText.replace(".", "");
            long moneyValue = Long.parseLong(cleanedMoneyText);
            long existingMoney = Long.parseLong(preferenceManager.getString(Constants.KEY_AMOUNT_MONEY));
            long existingMoneyBeneficiary = Long.parseLong(binding.textMoneyBeneficiary.getText().toString());
            long result = existingMoney - moneyValue, resultBeneficiary = existingMoneyBeneficiary + moneyValue;

            if (result >= 0) {
                updateAccountBalance(accountNumber, Long.toString(result));
                updateAccountBalance(accountNumberBeneficiary, Long.toString(resultBeneficiary));
                transfer();
            } else {
                showToast("Tài khoản của bạn không đủ để thực hiện");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadUserDetails() {
        binding.textNumberAccount.setText(preferenceManager.getString(Constants.KEY_ACCOUNT_NUMBER));
        binding.textVND.setText(preferenceManager.getString(Constants.KEY_AMOUNT_MONEY));
        binding.textNumberAccountBeneficiary.setText(getIntent().getStringExtra("ACCOUNT_NUMBER_BENEFICIARY"));
        binding.inputContent.setText(preferenceManager.getString(Constants.KEY_NAME) + " chuyển tiền");
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void isAccountBeneficiary() {
        String accountNumber = binding.textNumberAccountBeneficiary.getText().toString();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = database.collection(Constants.KEY_COLLECTION_USERS);

        Query query = usersCollection.whereEqualTo(Constants.KEY_ACCOUNT_NUMBER, accountNumber);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                    String name = documentSnapshot.getString(Constants.KEY_NAME);
                    String money = documentSnapshot.getString(Constants.KEY_AMOUNT_MONEY);

                    binding.textNameBeneficiary.setText(name);
                    binding.textMoneyBeneficiary.setText(money);
                } else {
                    showToast("Tài khoản thụ hưởng không tồn tại!");
                }
            } else {
                showToast("Lỗi khi truy vấn dữ liệu.");
            }
        });
    }

    private void transfer() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> transferContent = new HashMap<>();
        transferContent.put(Constants.KEY_ACCOUNT_NUMBER_TRANSFER, binding.textNumberAccount.getText().toString());
        transferContent.put(Constants.KEY_NAME_BENEFICIARY, binding.textNameBeneficiary.getText().toString());
        transferContent.put(Constants.KEY_ACCOUNT_NUMBER_BENEFICIARY, binding.textNumberAccountBeneficiary.getText().toString());
        transferContent.put(Constants.KEY_NUMBER_MONEY, Objects.requireNonNull(binding.inputMoney.getText()).toString());
        transferContent.put(Constants.KEY_TRANSFER_CONTENT, Objects.requireNonNull(binding.inputContent.getText()).toString());
        transferContent.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_TRANSFER)
                .add(transferContent)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putString(Constants.KEY_ACCOUNT_NUMBER_TRANSFER, binding.textNumberAccount.getText().toString());
                    preferenceManager.putString(Constants.KEY_NAME_BENEFICIARY, binding.textNameBeneficiary.getText().toString());
                    preferenceManager.putString(Constants.KEY_ACCOUNT_NUMBER_BENEFICIARY, binding.textNumberAccountBeneficiary.getText().toString());
                    preferenceManager.putString(Constants.KEY_NUMBER_MONEY, binding.inputMoney.getText().toString());
                    preferenceManager.putString(Constants.KEY_TRANSFER_CONTENT, binding.inputContent.getText().toString());
                    preferenceManager.putString(Constants.KEY_TIMESTAMP, String.valueOf(new Date()));

                    Intent intent = new Intent(getApplicationContext(), TransferContentActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private void updateAccountBalance(String accountNumber, String newMoney) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_ACCOUNT_NUMBER, accountNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String documentID = documentSnapshot.getId();

                        Map<String, Object> moneyBalance = new HashMap<>();
                        moneyBalance.put(Constants.KEY_AMOUNT_MONEY, newMoney);

                        database.collection(Constants.KEY_COLLECTION_USERS)
                                .document(documentID)
                                .update(moneyBalance);
                    } else {
                        showToast("Không tìm thấy tài khoản");
                    }
                });
    }

    private void decimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);

        binding.inputMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    try {
                        long amount = Long.parseLong(s.toString().replaceAll("\\.", ""));
                        String formattedAmount = decimalFormat.format(amount);
                        binding.inputMoney.removeTextChangedListener(this);
                        binding.inputMoney.setText(formattedAmount);
                        binding.inputMoney.setSelection(formattedAmount.length());
                        binding.inputMoney.addTextChangedListener(this);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        long amount = Long.parseLong(binding.textVND.getText().toString());
        String formattedAmount = decimalFormat.format(amount);
        binding.textVND.setText(formattedAmount);
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