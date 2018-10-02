package com.github.wallet.restwebservice.controller;

import com.github.wallet.restwebservice.advice.RateLimiterException;
import com.github.wallet.restwebservice.advice.WalletException;
import com.github.wallet.restwebservice.aspect.RateLimit;
import com.github.wallet.restwebservice.converter.WalletDtoConverter;
import com.github.wallet.restwebservice.service.contracts.IWalletService;
import com.github.wallet.restwebservice.service.models.WalletDTO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class WalletController {

    Logger logger = LoggerFactory.getLogger(WalletController.class);
    @Autowired
    private IWalletService walletService;
    private WalletDtoConverter walletDtoConverter = new WalletDtoConverter();

    @GetMapping("/wallet")
    @RateLimit(limit = 3, duration = 60, unit = TimeUnit.SECONDS)
    @ApiOperation(value = "Find all wallets", notes = "Returns a collection of wallets")
    public ResponseEntity<?> get() throws RateLimiterException {

        logger.info("WalletController get method calls for getting all wallets");

        List<WalletDTO> wallets = walletService.findAll();

        if(!wallets.isEmpty() && wallets.size() > 0){
            return ResponseEntity.ok().body(
                    wallets
                    .stream()
                    .collect(Collectors.toList())
            );
        }
        else{
            throw new WalletException(404, "Wallet not found");
        }
    }

    @GetMapping("/wallet/{walletId}")
    @RateLimit(limit = 3, duration = 60, unit = TimeUnit.SECONDS)
    @ApiOperation(value = "Find wallet of a given walletId", notes = "Returns a wallet by given walletId")
    public ResponseEntity<WalletDTO> getByWalletId(@PathVariable("walletId") long walletId) throws RateLimiterException{

        logger.info("walletController getByWalletId method calls for getting wallet from walletId");

        WalletDTO wallet = walletService.findById(walletId);

        if(wallet != null){
            return Optional.ofNullable(walletService.findById(walletId))
                    .map(w -> new ResponseEntity<>(
                            w,
                            HttpStatus.OK
                    )).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}/wallet")
    @RateLimit(limit = 3, duration = 60, unit = TimeUnit.SECONDS)
    @ApiOperation(value = "Find wallet of a given userId", notes = "Returns a wallet by given userId")
    public ResponseEntity<?> getByUserId(@PathVariable("userId") long userId) throws RateLimiterException{

        logger.info("walletController getByUserId method calls for getting wallet from userId");

        WalletDTO wallet = walletService.findByUserId(userId);

        if(wallet != null){
            return Optional.ofNullable(walletService.findByUserId(userId))
                    .map(w -> new ResponseEntity<>(
                            w,
                            HttpStatus.OK
                    )).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/user/{userId}/wallet/create")
    @RateLimit(limit = 3, duration = 60, unit = TimeUnit.SECONDS)
    @ApiOperation(value = "Create wallet of a given wallet", notes = "Create a wallet and return created wallet")
    public ResponseEntity<?> create(@PathVariable("userId") long userId, @RequestBody double balance) throws RateLimiterException{

        logger.info("walletController create method calls for creating wallet");
        if(balance < 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        WalletDTO createdWallet;

        try{
            createdWallet = walletService.createWallet(userId, balance);
            logger.info("walletController create method created wallet");
        }
        catch (WalletException exc){
            logger.error("walletController create method has an error");
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(createdWallet, HttpStatus.CREATED);
    }
}
