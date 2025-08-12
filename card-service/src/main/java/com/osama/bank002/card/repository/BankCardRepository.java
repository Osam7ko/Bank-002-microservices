package com.osama.bank002.card.repository;

import com.osama.bank002.card.domain.entity.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankCardRepository extends JpaRepository<BankCard,Long> {

    List<BankCard> findByAccountNumber(String accountNumber);
    Optional<BankCard> findByCardNumber(String cardNumber);
}