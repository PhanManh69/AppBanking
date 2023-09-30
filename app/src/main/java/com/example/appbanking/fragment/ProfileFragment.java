package com.example.appbanking.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.appbanking.R;
import com.example.appbanking.activities.ChangeEmailActivity;
import com.example.appbanking.activities.ChangeImageActivity;
import com.example.appbanking.activities.ChangePasswordActivity;
import com.example.appbanking.activities.InfoAccountActivity;
import com.example.appbanking.activities.LoginActivity;
import com.example.appbanking.utilities.Constants;
import com.example.appbanking.utilities.PreferenceManager;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private PreferenceManager preferenceManager;
    private TextView textHello, textName;
    private LinearLayout logout;
    private FrameLayout imageProfile, infoAccount, changePassword, changeEmail, changeName;
    private RoundedImageView imageUser;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        textHello = view.findViewById(R.id.textHello);
        textName = view.findViewById(R.id.textName);
        logout = view.findViewById(R.id.linearLayout);
        imageProfile = view.findViewById(R.id.imageProfile);
        imageUser = view.findViewById(R.id.imageUser);
        infoAccount = view.findViewById(R.id.infoAccount);
        changePassword = view.findViewById(R.id.changePassword);
        changeEmail = view.findViewById(R.id.changeEmail);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());

        setListeners();
        loadUserDetails();
    }

    private void setListeners() {
        logout.setOnClickListener(v -> signOutWithConfirmation());
        imageProfile.setOnClickListener(v -> startActivity(new Intent(getContext(), ChangeImageActivity.class)));
        infoAccount.setOnClickListener(v -> startActivity(new Intent(getContext(), InfoAccountActivity.class)));
        changePassword.setOnClickListener(v -> startActivity(new Intent(getContext(), ChangePasswordActivity.class)));
        changeEmail.setOnClickListener(v -> startActivity(new Intent(getContext(), ChangeEmailActivity.class)));
    }

    private void loadUserDetails() {
        textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hourOfDay >= 4 && hourOfDay < 12) {
            greeting = "Chào buổi sáng!";
        } else if (hourOfDay >= 12 && hourOfDay < 18) {
            greeting = "Chào buổi chiều!";
        } else {
            greeting = "Chào buổi tối!";
        }

        textHello.setText(greeting);

        byte[] bytes = android.util.Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE_PROFILE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageUser.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signOutWithConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setPositiveButton("Có", (dialog, which) -> {
                    signOut();
                })
                .setNegativeButton("Không", (dialog, which) -> {

                })
                .show();
    }

    private void signOut() {
        showToast("Đăng xuất ...");
        preferenceManager.clear();
        startActivity(new Intent(getContext(), LoginActivity.class));
    }
}