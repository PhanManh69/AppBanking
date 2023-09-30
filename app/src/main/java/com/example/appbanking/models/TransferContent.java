package com.example.appbanking.models;

import java.io.Serializable;
import java.util.Date;

public class TransferContent implements Serializable {
    public String accountNumberBeneficiary, contentTransfer, nameBeneficiary, numberMoney;
    private Date timestamp;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
