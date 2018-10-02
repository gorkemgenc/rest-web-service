package com.github.wallet.restwebservice.advice;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WalletException extends RuntimeException{

    private String detail;
    private int code;

    public WalletException(){}

    public WalletException(int code, String detail) {
        this.detail = detail;
        this.code = code;
    }
}
