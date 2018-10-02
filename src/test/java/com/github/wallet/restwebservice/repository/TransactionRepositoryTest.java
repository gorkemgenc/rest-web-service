package com.github.wallet.restwebservice.repository;

import com.github.wallet.restwebservice.advice.WalletException;
import com.github.wallet.restwebservice.models.Transaction;
import com.github.wallet.restwebservice.models.Type;
import com.github.wallet.restwebservice.models.Wallet;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@DataJpaTest
@Ignore
public class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TypeRepository transactionTypeRepository;
    @Autowired
    private WalletRepository walletRepository;

    private Wallet wallet1;
    private Wallet wallet2;
    private Type typeCredit;
    private Type typeDebit;
    private Transaction transaction;

    @Before
    public void before(){

        wallet1 = new Wallet(1,0);
        wallet2 = new Wallet(2,10);
        entityManager.persist(wallet1);
        entityManager.persist(wallet2);
        entityManager.flush();

        typeDebit = new Type(1,"Debit");
        typeCredit = new Type(2, "Credit");
        entityManager.persist(typeCredit);
        entityManager.persist(typeDebit);
        entityManager.flush();

        transaction = new Transaction(String.valueOf(UUID.randomUUID()),typeCredit, 5, wallet1);
        transaction.setId(100);
        entityManager.persist(transaction);
        entityManager.flush();
    }

    @Test
    public void testFindByWallet() {

        List<Transaction> transactions = transactionRepository.findByWalletId(wallet1.getId());
        assertTrue(transactions.size() > 0);
        assertEquals(transactions.get(0).getWallet().getId(),wallet1.getId());
        assertEquals(transactions.get(0).getId(),transaction.getId());
    }

    @Test
    public void testSave_Credit() {

        Transaction transaction = new Transaction(String.valueOf(UUID.randomUUID()),typeCredit,20,wallet2);
        Transaction found = transactionRepository.save(transaction);
        assertNotNull(found);
        assertEquals(found.getAmount(),20);
        assertEquals(found.getType().getId(), 2);
    }

    @Test
    public void testSave_Debit() {

        Transaction transaction = new Transaction(String.valueOf(UUID.randomUUID()),typeDebit,10,wallet2);
        Transaction found = transactionRepository.save(transaction);
        assertNotNull(found);
        assertEquals(found.getAmount(),-10);
        assertEquals(found.getType().getId(),1);
    }

    @Test
    public void testSave_NotUniqueGlobalId() {

        Transaction transaction = new Transaction(String.valueOf(-1),typeDebit,10,wallet2);
        try{
            transactionRepository.save(transaction);
            entityManager.flush();
            fail();
        } catch(WalletException ex){
            assertEquals(ex.getCode(),400);
        }
    }

    @Test
    public void whenSave_NoBalance() {

        Transaction transaction = new Transaction(String.valueOf(UUID.randomUUID()),typeCredit,10,wallet2);
        try{
            Transaction found = transactionRepository.save(transaction);
            entityManager.flush();
            fail();
        } catch(ConstraintViolationException ex){
            assertFalse(ex.getConstraintViolations().isEmpty());
            assertTrue(ex.getConstraintViolations().iterator().next().getMessage().contains("Transaction amount must be provided"));

        }
    }

    @Test
    public void whenSave_FailWrongWallet() {

        long walletId = 100;
        Wallet wallet = walletRepository.getOne(walletId);
        Transaction transaction = new Transaction(String.valueOf(UUID.randomUUID()),typeDebit,10,wallet);
        try{
            Transaction found = transactionRepository.save(transaction);
            entityManager.flush();
            fail();
        } catch(DataIntegrityViolationException ex){
            assertTrue( ex.getMessage().contains("could not execute statement"));
        }
    }
}