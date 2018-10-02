package com.github.wallet.restwebservice.service;

import com.github.wallet.restwebservice.advice.WalletException;
import com.github.wallet.restwebservice.converter.TransactionDtoConverter;
import com.github.wallet.restwebservice.models.Transaction;
import com.github.wallet.restwebservice.models.Wallet;
import com.github.wallet.restwebservice.repository.TransactionRepository;
import com.github.wallet.restwebservice.repository.TypeRepository;
import com.github.wallet.restwebservice.repository.WalletRepository;
import com.github.wallet.restwebservice.service.contracts.ITransactionService;
import com.github.wallet.restwebservice.service.models.TransactionDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@PropertySource("classpath:application.properties")
public class TransactionService implements ITransactionService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private WalletService walletService;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TypeRepository typeRepository;
    private TransactionDtoConverter transactionConverter = new TransactionDtoConverter();

    @Transactional(rollbackFor = WalletException.class)
    @Override
    public List<TransactionDTO> getTransactionsByWalletId(@NotNull long walletId) throws WalletException {

        try {
            return  transactionRepository.findByWalletId(walletId).stream().map(w -> transactionConverter.convert(w)).collect(Collectors.toList());
        } catch (Exception e){
            throw new WalletException(400, "WalletId does not exists - getTransactionsByWalletId method");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = WalletException.class)
    @Override
    public TransactionDTO createTransactionAndChangeBalance(@NotBlank String globalId, @NotBlank int typeId, @NotNull double amount, @NotNull long walletId) throws WalletException {

        try {
            if(typeId == 1){
                walletService.deleteWalletAmount(walletId, amount);
            }
            else if(typeId == 2){

                walletService.addWalletAmount(walletId, amount);
            }
            Optional<Wallet> addedWallet = walletRepository.findById(walletId);

            if(addedWallet.isPresent()){

                Transaction addTransaction = new Transaction(globalId,typeRepository.getOne(typeId),amount,addedWallet.get());
                Transaction createdTransaction =  transactionRepository.save(addTransaction);
                if(createdTransaction != null){
                    return transactionConverter.convert(createdTransaction);
                }
                else{
                    throw new WalletException(400, "Transaction couldn't be created. - createTransactionAndChangeBalance method");
                }
            }
            else{
                throw  new WalletException(400, "Transaction couldn't be created. No suitable wallet exists - createTransactionAndChangeBalance method");
            }

        } catch(NumberFormatException e){
            throw new WalletException(400, "Format Exception");
        }
    }
}
