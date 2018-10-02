package com.github.wallet.restwebservice.service;

import com.github.wallet.restwebservice.advice.WalletException;
import com.github.wallet.restwebservice.models.Wallet;
import com.github.wallet.restwebservice.repository.TransactionRepository;
import com.github.wallet.restwebservice.repository.WalletRepository;
import com.github.wallet.restwebservice.service.models.WalletDTO;
import org.junit.Before;
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
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
public class WalletServiceTest {

    @TestConfiguration
    static class WalletServiceTestContextConfiguration {

        @Bean
        public WalletService walletService() {
            return new WalletService();
        }
        @Bean
        public MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }

    @Autowired
    private WalletService walletService;
    @MockBean
    private WalletRepository walletRepository;
    @MockBean
    private TransactionRepository transactionRepository;
    Wallet wallet1;
    Wallet wallet2;

    @Before
    public void setUp() {

        wallet1 = new Wallet(1,10);
        wallet2 = new Wallet(2,20);
        wallet1.setId(1);
        wallet2.setId(2);
        createMockito();
    }

    @Test
    public void testFindAll() throws WalletException {

        List<WalletDTO> wallet = walletService.findAll();
        assertNotNull(wallet);
        assertEquals(wallet.get(0).getId(),wallet1.getId());
        assertEquals(wallet.get(1).getId(),wallet2.getId());
    }

    @Test
    public void testFindById() throws WalletException {

        WalletDTO wallet = walletService.findById(wallet1.getId());
        assertNotNull(wallet);
        assertEquals(wallet.getId(),wallet1.getId());
    }

    @Test
    public void testUpdateBalance() throws WalletException {

        WalletDTO wallet = walletService.addWalletAmount(wallet1.getId(),30);
        assertEquals(wallet.getId(),wallet1.getId());
        assertEquals(wallet.getBalance(),40, 0.0001);
    }

    @Test
    public void testUpdateBalanceDebitSuccess() throws WalletException {

        WalletDTO wallet = walletService.deleteWalletAmount(wallet1.getId(),10);
        assertEquals(wallet.getUserId(),wallet1.getUserId());
        assertEquals(wallet.getBalance(),0.0, 0.0001);
    }

    @Test
    public void testUpdateBalanceDebitFailure() throws WalletException {

        int amount = 100;
        try {
            walletService.deleteWalletAmount(wallet2.getId(), amount);
            fail();
        } catch (WalletException ex){
            assertEquals(ex.getCode(),400);
        }
    }

    private void createMockito(){

        Mockito.when(walletRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(wallet1, wallet2));
        Mockito.when(walletRepository.findById(wallet1.getId())).thenReturn(Optional.of(wallet1));
        Mockito.when(walletRepository.findById(wallet2.getId())).thenReturn(Optional.of(wallet2));
        Mockito.when(walletRepository.findByUserId(1)).thenReturn(wallet1);
        Mockito.when(walletRepository.save(wallet1)).thenReturn(wallet1);
    }
}
