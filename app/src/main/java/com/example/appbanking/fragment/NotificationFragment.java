package com.example.appbanking.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbanking.R;
import com.example.appbanking.adapters.NotificationAdapter;
import com.example.appbanking.models.TransferContent;
import com.example.appbanking.utilities.Constants;
import com.example.appbanking.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {
    private PreferenceManager preferenceManager;
    private TextView textErrorMessage;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        textErrorMessage = view.findViewById(R.id.textErrorMessage);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());

        getContent();
    }

    private void getContent() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_TRANSFER)
                .orderBy(Constants.KEY_TIMESTAMP, Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_ACCOUNT_NUMBER);

                    if (error != null) {
                        showErrorMessage();
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        List<TransferContent> transferContents = new ArrayList<>();

                        for (QueryDocumentSnapshot queryDocumentSnapshot : value) {
                            String accountNumberTransfer = queryDocumentSnapshot.getString(Constants.KEY_ACCOUNT_NUMBER_BENEFICIARY);

                            if (currentUserId.equals(accountNumberTransfer)) {
                                TransferContent transferContent = new TransferContent();
                                transferContent.nameBeneficiary = queryDocumentSnapshot.getString(Constants.KEY_NAME_BENEFICIARY);
                                transferContent.accountNumberBeneficiary = queryDocumentSnapshot.getString(Constants.KEY_ACCOUNT_NUMBER_BENEFICIARY);
                                transferContent.contentTransfer = queryDocumentSnapshot.getString(Constants.KEY_TRANSFER_CONTENT);
                                transferContent.numberMoney = queryDocumentSnapshot.getString(Constants.KEY_NUMBER_MONEY);
                                Date timestamp = queryDocumentSnapshot.getDate(Constants.KEY_TIMESTAMP);
                                transferContent.setTimestamp(timestamp);

                                transferContents.add(transferContent);
                            }
                        }

                        if (transferContents.size() > 0) {
                            NotificationAdapter notificationAdapter = new NotificationAdapter(transferContents);
                            recyclerView.setAdapter(notificationAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage() {
        textErrorMessage.setText(String.format("%s", "Không có dữ liệu"));
        textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}