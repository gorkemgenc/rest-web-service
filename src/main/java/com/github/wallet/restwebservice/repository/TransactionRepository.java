package com.github.wallet.restwebservice.repository;

import com.github.wallet.restwebservice.advice.WalletException;
import com.github.wallet.restwebservice.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional(rollbackOn = WalletException.class)
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(long walletId);
    Transaction findByGlobalId(String globalId);
}
