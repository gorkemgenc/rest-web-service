package com.github.wallet.restwebservice.repository;

import com.github.wallet.restwebservice.advice.WalletException;
import com.github.wallet.restwebservice.models.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository
@Transactional(rollbackOn = WalletException.class)
public interface TypeRepository extends JpaRepository<Type, Integer> {
}
