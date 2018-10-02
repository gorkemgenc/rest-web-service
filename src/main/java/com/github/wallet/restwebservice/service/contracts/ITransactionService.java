package com.github.wallet.restwebservice.service.contracts;

import com.github.wallet.restwebservice.advice.WalletException;
import com.github.wallet.restwebservice.service.models.TransactionDTO;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface ITransactionService {

    List<TransactionDTO> getTransactionsByWalletId(@NotNull long walletId) throws WalletException;
    TransactionDTO createTransactionAndChangeBalance(String globalId, int typeId, double amount, long walletId) throws WalletException;
}
