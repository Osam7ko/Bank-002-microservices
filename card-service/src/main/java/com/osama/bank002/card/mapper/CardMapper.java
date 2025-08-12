package com.osama.bank002.card.mapper;

import com.osama.bank002.card.domain.dto.CardDto;
import com.osama.bank002.card.domain.entity.BankCard;

public final class CardMapper {
    private CardMapper(){}

    public static CardDto toDto(BankCard c) {
        return new CardDto(
                c.getId(),
                mask(c.getCardNumber()),
                c.getAccountNumber(),
                c.getCardType(),
                c.getStatus(),
                c.getExpiryMonth(),
                c.getExpiryYear(),
                c.getNameOnCard(),
                c.getCreatedAt()
        );
    }

    public static String mask(String pan) {
        if (pan == null || pan.length() < 10) return "****";
        return pan.substring(0, 6) + "******" + pan.substring(pan.length()-4);
    }
}