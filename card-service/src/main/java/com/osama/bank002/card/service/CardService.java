package com.osama.bank002.card.service;

import com.osama.bank002.card.domain.dto.CardDto;
import com.osama.bank002.card.domain.dto.IssueCardRequest;
import com.osama.bank002.card.domain.dto.VerifyCardRequest;
import com.osama.bank002.card.domain.dto.VerifyCardResponse;

import java.util.List;

public interface CardService {

    CardDto issue(IssueCardRequest req);

    List<CardDto> listByAccount(String accountNumber);

    void changeStatus(Long cardId, String status);

    // For the payment Service
    VerifyCardResponse verify(VerifyCardRequest req);

}