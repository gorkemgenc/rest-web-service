package com.github.wallet.restwebservice.repository;

import com.github.wallet.restwebservice.models.Wallet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class WalletRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private WalletRepository walletRepository;
    private Wallet wallet1;
    private Wallet wallet2;

    @Before
    public void before(){

        wallet1 = new Wallet(1,10);
        wallet2 = new Wallet(2,20);
        entityManager.persist(wallet1);
        entityManager.persist(wallet2);
        entityManager.flush();
    }

    @Test
    public void testFindByWalletIdIfWallet() {

        Optional<Wallet> wallet = walletRepository.findById(wallet1.getId());
        assertTrue(wallet.isPresent());
        assertEquals(wallet.get().getBalance(),wallet1.getBalance(),0.001);
        assertEquals(wallet.get().getUserId(),wallet1.getUserId());
    }

    @Test
    public void testFindByWalletIdIfNotExistWallet() {

        long walletId = 100;
        Optional<Wallet> found = walletRepository.findById(walletId);
        assertTrue(!found.isPresent());
    }

    @Test
    public void testFindByUserIdIfWallet() {

        long userId = 1;
        Wallet wallet = walletRepository.findByUserId(userId);
        assertEquals(wallet.getUserId(),1);
        assertEquals(wallet.getBalance(),10, 0.0001);
    }

    @Test
    public void testFindAllByOrderByIdAsc() {
        List<Wallet> found = walletRepository.findAllByOrderByIdAsc();
        assertNotNull(found);
        assertTrue(!found.isEmpty());
        assertTrue(found.size() >= 2);
        assertEquals(found.get(0).getId(),wallet1.getId());
        assertEquals(found.get(1).getId(),wallet2.getId());
    }

    @Test
    public void testUpdateBalance() {

        Optional<Wallet> found = walletRepository.findById(wallet1.getId());
        Wallet updated = found.get();
        updated.setBalance(100);
        Wallet found1 = walletRepository.save(updated);
        assertNotNull(found1);
        assertEquals(found1.getBalance(),100, 0.0001);
    }

    @After
    public void after(){
    }


}
