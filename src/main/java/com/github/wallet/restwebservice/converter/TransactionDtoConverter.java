package com.github.wallet.restwebservice.converter;

import com.github.wallet.restwebservice.models.Transaction;
import com.github.wallet.restwebservice.service.models.TransactionDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TransactionDtoConverter implements Converter<Transaction, TransactionDTO> {

    @Override
    public TransactionDTO convert(Transaction transaction) {

        return TransactionDTO.builder()
                .globalId(transaction.getGlobalId())
                .walletId(transaction.getWallet().getId())
                .amount(transaction.getAmount())
                .typeId(transaction.getType().getId())
                .build();
    }
}
