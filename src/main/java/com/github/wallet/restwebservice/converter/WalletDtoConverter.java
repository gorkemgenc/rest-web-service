package com.github.wallet.restwebservice.converter;

import com.github.wallet.restwebservice.models.Wallet;
import com.github.wallet.restwebservice.service.models.WalletDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WalletDtoConverter implements Converter<Wallet, WalletDTO> {

    @Override
    public WalletDTO convert(Wallet wallet) {

        return WalletDTO.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .lastUpdated(wallet.getLastUpdated())
                .build();
    }
}
