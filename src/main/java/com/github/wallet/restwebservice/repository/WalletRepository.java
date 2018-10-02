package com.github.wallet.restwebservice.repository;

import com.github.wallet.restwebservice.advice.WalletException;
import com.github.wallet.restwebservice.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional(rollbackOn = WalletException.class)
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findAllByOrderByIdAsc();
    Wallet findByUserId(long userId);

}
