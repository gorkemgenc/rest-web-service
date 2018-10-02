package com.github.wallet.restwebservice.service.models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Setter
@Getter
@Builder
public class TransactionDTO {

    private String globalId;
    private int typeId;
    private double amount;
    private long walletId;

    public TransactionDTO(){ }

    public TransactionDTO(String globalId, int typeId, double amount, long walletId){
        this.globalId = globalId;
        this.typeId = typeId;
        this.amount = amount;
        this.walletId = walletId;
    }
}
