package com.osama.bank002.card.util;

import java.util.concurrent.ThreadLocalRandom;

public final class PanUtils {
    private PanUtils(){}

    // issuer BIN (example only—pick yours). For VISA start with 4; MASTER usually 51-55/2221-2720.
    private static final String BIN = "400202"; // dev/demo

    public static String generatePan(){
        // BIN(6) + accountId(9 random) + checkDigit(1) = 16
        String body = BIN + String.format("%09d", ThreadLocalRandom.current().nextLong(0,1_000_000_000L));
        int check = luhnCheckDigit(body);
        return body + check;
    }

    private static int luhnCheckDigit(String number){
        int sum = 0;
        boolean alt = true;
        for (int i = number.length()-1;i >= 0; i--){
            int n = number.charAt(i) - '0';
            if (alt) { n *= 2; if ( n > 9) n-=9 ;}
            sum += n;
            alt = !alt;
        }
        return (10 - (sum % 10)) % 10;
    }
}