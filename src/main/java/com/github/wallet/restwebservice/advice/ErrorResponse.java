package com.github.wallet.restwebservice.advice;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class ErrorResponse {

    private String exception;
    private int code;

    public ErrorResponse(){}
    public ErrorResponse(String exception, int code){
        this.exception = exception;
        this.code = code;
    }
}
