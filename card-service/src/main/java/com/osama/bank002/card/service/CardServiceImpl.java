package com.osama.bank002.card.service;

import com.osama.bank002.card.client.AccountClient;
import com.osama.bank002.card.client.dto.AccountDto;
import com.osama.bank002.card.domain.dto.CardDto;
import com.osama.bank002.card.domain.dto.IssueCardRequest;
import com.osama.bank002.card.domain.dto.VerifyCardRequest;
import com.osama.bank002.card.domain.dto.VerifyCardResponse;
import com.osama.bank002.card.domain.entity.BankCard;
import com.osama.bank002.card.mapper.CardMapper;
import com.osama.bank002.card.repository.BankCardRepository;
import com.osama.bank002.card.util.CryptoUtils;
import com.osama.bank002.card.util.PanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class CardServiceImpl implements CardService{

    private final BankCardRepository repo;
    private final AccountClient accountClient;
    private final CryptoUtils crypto;

    @Transactional
    @Override
    public CardDto issue(IssueCardRequest req) {
        // 1) sanity check account
        AccountDto acc = accountClient.get(req.accountNumber());
        if (acc == null || !"ACTIVE".equalsIgnoreCase(acc.status())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Account not active or not found");
        }

        // 2) generate PAN
        String pan = PanUtils.generatePan();

        // 3) hash CVV
        String cvvHash = crypto.sha256(req.cvv());

        // 4) build signature (bank-side)
        //    Signature covers core fields so payment-service can trust it without DB:
        //    data = PAN|MM|YYYY|accountNumber
        String data = String.join("|", pan, req.expiryMonth(), req.expiryYear(), req.accountNumber());
        String sign = crypto.hmacSha256(data);

        BankCard card = BankCard.builder()
                .cardNumber(pan)
                .accountNumber(req.accountNumber())
                .expiryMonth(req.expiryMonth())
                .expiryYear(req.expiryYear())
                .cvvHash(cvvHash)
                .cardType(req.cardType().toUpperCase())
                .status("ACTIVE")
                .signature(sign)
                .nameOnCard(req.nameOnCard())
                .build();

        repo.save(card);
        return CardMapper.toDto(card);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CardDto> listByAccount(String accountNumber) {
        return repo.findByAccountNumber(accountNumber).stream().map(CardMapper::toDto).toList();
    }

    @Transactional
    @Override
    public void changeStatus(Long cardId, String status) {
        BankCard c = repo.findById(cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));
        c.setStatus(status.toUpperCase());
    }

    @Transactional(readOnly = true)
    @Override
    public VerifyCardResponse verify(VerifyCardRequest req) {
        BankCard c = repo.findByCardNumber(req.cardNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));

        if (!"ACTIVE".equalsIgnoreCase(c.getStatus()))
            return new VerifyCardResponse(false, "CARD_NOT_ACTIVE", null);

        YearMonth exp = YearMonth.of(Integer.parseInt(c.getExpiryYear()), Integer.parseInt(c.getExpiryMonth()));
        if (YearMonth.now().isAfter(exp))
            return new VerifyCardResponse(false, "EXPIRED", null);

        // cvv
        if (!crypto.safeEquals(crypto.sha256(req.cvv()), c.getCvvHash()))
            return new VerifyCardResponse(false, "CVV_MISMATCH", null);

        // signature integrity
        String data = String.join("|", c.getCardNumber(), c.getExpiryMonth(), c.getExpiryYear(), c.getAccountNumber());
        if (!crypto.safeEquals(crypto.hmacSha256(data), c.getSignature()))
            return new VerifyCardResponse(false, "SIGNATURE_INVALID", null);

        return new VerifyCardResponse(true, "OK", c.getSignature());
    }
}