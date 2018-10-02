package com.github.wallet.restwebservice.service;

import com.github.wallet.restwebservice.advice.WalletException;
import com.github.wallet.restwebservice.converter.WalletDtoConverter;
import com.github.wallet.restwebservice.models.Transaction;
import com.github.wallet.restwebservice.models.Type;
import com.github.wallet.restwebservice.models.Wallet;
import com.github.wallet.restwebservice.repository.TransactionRepository;
import com.github.wallet.restwebservice.repository.TypeRepository;
import com.github.wallet.restwebservice.repository.WalletRepository;
import com.github.wallet.restwebservice.service.models.TransactionDTO;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class TransactionServiceTest {

    @TestConfiguration
    static class TransactionServiceTestContextConfiguration {

        @Bean
        public TransactionService transactionService() {
            return new TransactionService();
        }
        @Bean
        public MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }

    static int globalIdCounter = 1;
    @Autowired
    private TransactionService transactionService;
    private WalletDtoConverter walletDtoConverter = new WalletDtoConverter();
    @MockBean
    private WalletRepository walletRepository;
    @MockBean
    private TransactionRepository transactionRepository;
    @MockBean
    private TypeRepository transactionTypeRepository;
    @MockBean
    private WalletService walletService;

    Type typeCredit;
    Type typeDebit;
    private Wallet wallet1;
    private Wallet wallet2;
    private Transaction transactionCredit;
    private Transaction transactionDebit;

    @Before
    public void setUp() throws WalletException {

        wallet1 = new Wallet(1,0);
        wallet2 = new Wallet(2,10);
        wallet1.setId(1);
        wallet2.setId(2);
        typeDebit = new Type(1,"Debit");
        typeCredit = new Type(2, "Credit");
        transactionCredit = new Transaction(String.valueOf(globalIdCounter++),typeDebit,20,wallet1);
        transactionCredit.setId(5);
        transactionDebit = new Transaction(String.valueOf(globalIdCounter++), typeCredit,10,wallet2);
        transactionDebit.setId(6);
        createMockito();
    }

    @Test
    public void testGetTransactionsByWalletId_Success() throws WalletException {
        List<TransactionDTO> found = transactionService.getTransactionsByWalletId(wallet1.getId());
        assertNotNull(found);
        assertTrue(found.size() == 1);
    }

    @Test
    public void testCreateTransaction_DebitFailure() throws WalletException {

        int amount = 100;
        int counter = globalIdCounter++;
        Mockito.when(walletService.deleteWalletAmount(wallet2.getId(),amount)).
                thenThrow(new WalletException());
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transactionDebit);
    }
    @Test
    @Ignore
    public void testCreateTransaction_SuccessCredit() throws WalletException {

        int amount = 100;
        Mockito.when(walletService.addWalletAmount(wallet1.getId(),amount)).thenReturn(walletDtoConverter.convert(wallet1));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transactionCredit);
        int counter = globalIdCounter++;
        TransactionDTO found = transactionService.createTransactionAndChangeBalance(String.valueOf(counter),typeCredit.getId(),amount,wallet1.getId());
        assertNotNull(found);
    }

    @Test
    @Ignore
    public void testCreateTransaction_SuccessDebit() throws WalletException {

        int amount = -10;
        Mockito.when(walletService.deleteWalletAmount(wallet1.getId(),-1*amount)).thenReturn(walletDtoConverter.convert(wallet1));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transactionCredit);
        int counter = globalIdCounter++;
        TransactionDTO found = transactionService.createTransactionAndChangeBalance(String.valueOf(counter),typeCredit.getId(),amount,wallet1.getId());
        assertNotNull(found);
    }

    private void createMockito(){

        Mockito.when(walletService.findById(wallet1.getId())).thenReturn(walletDtoConverter.convert(wallet1));
        Mockito.when(transactionRepository.findByWalletId(wallet1.getId())).thenReturn(Arrays.asList(transactionCredit));
        Mockito.when(transactionRepository.findByWalletId(wallet2.getId())).thenReturn(Arrays.asList(transactionCredit));
        Mockito.when(transactionTypeRepository.getOne(typeCredit.getId())).thenReturn(typeCredit);
        Mockito.when(transactionTypeRepository.getOne(typeDebit.getId())).thenReturn(typeDebit);
        Mockito.when(walletService.findById(wallet1.getId())).thenReturn(walletDtoConverter.convert(wallet1));
        Mockito.when(walletService.findById(wallet2.getId())).thenReturn(walletDtoConverter.convert(wallet2));
    }
}
