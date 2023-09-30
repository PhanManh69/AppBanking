package com.example.appbanking.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbanking.databinding.ItemContentTransferBinding;
import com.example.appbanking.models.TransferContent;

import java.util.List;

public class ContentTransferAdapter extends  RecyclerView.Adapter<ContentTransferAdapter.ContentViewHolder> {
    private final List<TransferContent> transferContents;

    public ContentTransferAdapter(List<TransferContent> transferContents) {
        this.transferContents = transferContents;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContentTransferBinding itemContentTransferBinding = ItemContentTransferBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ContentViewHolder(itemContentTransferBinding);
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
        ItemContentTransferBinding binding;

        ContentViewHolder(ItemContentTransferBinding itemContentTransferBinding) {
            super(itemContentTransferBinding.getRoot());
            binding = itemContentTransferBinding;
        }

        @SuppressLint("SetTextI18n")
        void setContentData(TransferContent transferContent) {
            binding.textTime.setText("Thời gian: " + transferContent.getTimestamp());
            binding.textName.setText("Người nhận: " + transferContent.nameBeneficiary);
            binding.textNumberAccount.setText("Số tài khoản: " + transferContent.accountNumberBeneficiary);
            binding.textContent.setText("Nội dung: " + transferContent.contentTransfer);
            binding.textNumberMoney.setText("-" + transferContent.numberMoney);
        }
    }
}
