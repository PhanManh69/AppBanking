package com.example.appbanking.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appbanking.R;
import com.example.appbanking.databinding.ActivityMainBinding;
import com.example.appbanking.fragment.HistoryFragment;
import com.example.appbanking.fragment.HomeFragment;
import com.example.appbanking.fragment.NotificationFragment;
import com.example.appbanking.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menuHome) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.menuHistory) {
                replaceFragment(new HistoryFragment());
            } else if (item.getItemId() == R.id.menuNotification) {
                replaceFragment(new NotificationFragment());
            } else if (item.getItemId() == R.id.menuProfile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });

        setListeners();
    }

    private void setListeners() {
        binding.qrCode.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), QRCodeActivity.class)));
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}