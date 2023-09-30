package com.example.appbanking.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbanking.databinding.ItemNotificationBinding;
import com.example.appbanking.models.TransferContent;

import java.util.List;

public class NotificationAdapter extends  RecyclerView.Adapter<NotificationAdapter.ContentViewHolder> {
    private final List<TransferContent> transferContents;

    public NotificationAdapter(List<TransferContent> transferContents) {
        this.transferContents = transferContents;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding itemNotificationBinding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ContentViewHolder(itemNotificationBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.setContentData(transferContents.get(position));
    }

    @Override
    public int getItemCount() {
        return transferContents.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;

        ContentViewHolder(ItemNotificationBinding itemNotificationBinding) {
            super(itemNotificationBinding.getRoot());
            binding = itemNotificationBinding;
        }

        @SuppressLint("SetTextI18n")
        void setContentData(TransferContent transferContent) {
            binding.textTime.setText("Thời gian: " + transferContent.getTimestamp());
            binding.textName.setText("Người nhận: " + transferContent.nameBeneficiary);
            binding.textNumberAccount.setText("Số tài khoản: " + transferContent.accountNumberBeneficiary);
            binding.textContent.setText("Nội dung: " + transferContent.contentTransfer);
            binding.textNumberMoney.setText("+" + transferContent.numberMoney);
        }
    }
}
