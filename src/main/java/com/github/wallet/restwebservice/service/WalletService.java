package com.github.wallet.restwebservice.service;

import com.github.wallet.restwebservice.advice.WalletException;
import com.github.wallet.restwebservice.converter.WalletDtoConverter;
import com.github.wallet.restwebservice.models.Wallet;
import com.github.wallet.restwebservice.repository.TransactionRepository;
import com.github.wallet.restwebservice.repository.WalletRepository;
import com.github.wallet.restwebservice.service.contracts.IWalletService;
import com.github.wallet.restwebservice.service.models.WalletDTO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@PropertySource("classpath:application.properties")
public class WalletService implements IWalletService {

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    private WalletDtoConverter walletDtoConverter = new WalletDtoConverter();

    @Override
    @Transactional(rollbackFor = WalletException.class)
    public List<WalletDTO> findAll() throws WalletException{

        log.info("Getting all wallet");

        return walletRepository.findAllByOrderByIdAsc().stream().map(w -> walletDtoConverter.convert(w)).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = WalletException.class)
    public WalletDTO findById(@NotNull long id) throws WalletException{

        Optional<Wallet> optionalWallet =  walletRepository.findById(id);

        if(optionalWallet.isPresent()){
            return walletDtoConverter.convert(optionalWallet.get());
        }else{
            throw new WalletException(404, "Wallet Not Found Exception");
        }
    }

    @Override
    public WalletDTO findByUserId(@NotNull long userId) throws WalletException{

        Wallet createdWallet = walletRepository.findByUserId(userId);
        if(createdWallet == null) return null;
        return walletDtoConverter.convert(createdWallet);
    }

    @Override
    @Transactional(rollbackFor = WalletException.class)
    public WalletDTO createWallet(@NotNull long userId, @NotNull double balance) throws WalletException {

        try{

            Wallet existWallet = walletRepository.findByUserId(userId);
            if(existWallet != null) throw new WalletException(409, "Wallet is already exists - createWallet method");
            Wallet addWallet = new Wallet(userId, balance);
            Wallet createdWallet = walletRepository.save(addWallet);
            if(createdWallet != null){
                return walletDtoConverter.convert(createdWallet);
            }else{
                throw  new WalletException(422, "Wallet could not be created");
            }
        }
        catch (ObjectNotFoundException e){
            throw new WalletException(404,"No Suitable Wallet found");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = WalletException.class)
    public WalletDTO addWalletAmount(@NotNull long walletId, @NotNull double amount) throws WalletException {

        try{
            Optional<Wallet> currentWallet = walletRepository.findById(walletId);
            if(!currentWallet.isPresent()) throw new WalletException(404, "addWalletAmount could not work - addWalletAmount method");
            double currentBalance = currentWallet.get().getBalance();
            currentWallet.get().setBalance(currentBalance + amount);
            currentWallet.get().setLastUpdated(new Date());
            Wallet updatedWallet = walletRepository.save(currentWallet.get());

            if(updatedWallet != null){
                return walletDtoConverter.convert(updatedWallet);
            }else{
              throw  new WalletException(404, "addWalletAmount could not work - addWalletAmount method");
            }
        }
        catch (Exception ex){
            throw  new WalletException(400, "addWalletAmount method exception");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = WalletException.class)
    public WalletDTO deleteWalletAmount(@NotNull long walletId, @NotNull double amount) throws WalletException {

        try{
            Optional<Wallet> currentWallet = walletRepository.findById(walletId);
            if(!currentWallet.isPresent()) throw  new WalletException(404, "addWalletAmount could not work - addWalletAmount method");

            double currentBalance = currentWallet.get().getBalance();
            if(currentBalance < amount) throw  new WalletException(404, "No enough balance for this operation");
            currentWallet.get().setBalance(currentBalance - amount);
            currentWallet.get().setLastUpdated(new Date());
            Wallet updatedWallet = walletRepository.save(currentWallet.get());
            if(updatedWallet != null){
                return walletDtoConverter.convert(updatedWallet);
            }else{
                throw  new WalletException(404, "addWalletAmount could not work - addWalletAmount method");
            }
        }
        catch (Exception ex){
            throw  new WalletException(400, "addWalletAmount method exception");
        }
    }
}
