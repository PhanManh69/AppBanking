package com.example.appbanking.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.appbanking.R;
import com.example.appbanking.activities.TransferMoneyActivity;
import com.example.appbanking.utilities.Constants;
import com.example.appbanking.utilities.PreferenceManager;
import com.ibm.icu.text.Transliterator;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private PreferenceManager preferenceManager;
    private TextView textName, textName1, textHello, textNumberAccount, textRemainingMoney;
    private HorizontalScrollView horizontalScrollView;
    private ImageButton imageTransferMoney;
    private RoundedImageView imageUser;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        textName = view.findViewById(R.id.textName);
        textName1 = view.findViewById(R.id.textName1);
        textHello = view.findViewById(R.id.textHello);
        textNumberAccount = view.findViewById(R.id.textNumberAccount);
        horizontalScrollView = view.findViewById(R.id.horizontalScrollView);
        imageTransferMoney = view.findViewById(R.id.imageTransferMoney);
        textRemainingMoney = view.findViewById(R.id.textRemainingMoney);
        imageUser = view.findViewById(R.id.imageUser);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());

        Transliterator transliterator = Transliterator.getInstance("NFD; [:Diacritic:] Remove; NFC");
        String nameWithDiacritics = preferenceManager.getString(Constants.KEY_NAME);
        String nameWithoutDiacritics = transliterator.transliterate(nameWithDiacritics);
        textName.setText(nameWithoutDiacritics);

        textName1.setText(preferenceManager.getString(Constants.KEY_NAME));
        textRemainingMoney.setText(preferenceManager.getString(Constants.KEY_AMOUNT_MONEY));
        textNumberAccount.setText(preferenceManager.getString(Constants.KEY_ACCOUNT_NUMBER));

        byte[] bytes = android.util.Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE_PROFILE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageUser.setImageBitmap(bitmap);

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

        imageTransferMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TransferMoneyActivity.class);
                startActivity(intent);
            }
        });

        decimalFormat();
    }

    private void decimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);

        long amount = Long.parseLong(textRemainingMoney.getText().toString());
        String formattedAmount = decimalFormat.format(amount);
        textRemainingMoney.setText(formattedAmount);
    }
}