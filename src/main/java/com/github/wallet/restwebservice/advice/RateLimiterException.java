package com.github.wallet.restwebservice.advice;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RateLimiterException extends RuntimeException {

    private String detail;
    private int code;

    public RateLimiterException(String detail, int code){
        this.code = code;
        this.detail = detail;
    }
}
