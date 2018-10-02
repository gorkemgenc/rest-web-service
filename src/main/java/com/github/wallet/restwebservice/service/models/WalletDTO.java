package com.github.wallet.restwebservice.service.models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Data
@Setter
@Getter
@Builder
public class WalletDTO {

    private long id;
    private long userId;
    private double balance;
    private Date lastUpdated;

}
