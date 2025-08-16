package com.osama.bank002.account.repository;

import com.osama.bank002.account.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByAccountNumber(String accountNumber);

    List<BankAccount> findByProfileIdOrderByCreatedAtAsc(String profileId);

    int countByProfileIdAndStatusIn(String profileId, Collection<String> statuses);

    boolean existsByAccountNumber(String accountNumber);

    int countByProfileId(String profileId);
}