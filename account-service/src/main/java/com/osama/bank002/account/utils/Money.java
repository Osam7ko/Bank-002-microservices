package com.osama.bank002.account.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {
    private Money(){}

    public static BigDecimal normalize(BigDecimal v){
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}