package com.github.wallet.restwebservice.service.contracts;

import com.github.wallet.restwebservice.advice.WalletException;
import com.github.wallet.restwebservice.service.models.WalletDTO;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface IWalletService {

    List<WalletDTO> findAll() throws WalletException;
    WalletDTO findById(@NotNull long id) throws WalletException;
    WalletDTO findByUserId(@NotNull long userId) throws WalletException;
    WalletDTO createWallet(@NotNull long userId, @NotNull double balance) throws WalletException;
    WalletDTO addWalletAmount(@NotNull long walletId,@NotNull double amount) throws WalletException;
    WalletDTO deleteWalletAmount(@NotNull long walletId,@NotNull double amount) throws WalletException;
}
