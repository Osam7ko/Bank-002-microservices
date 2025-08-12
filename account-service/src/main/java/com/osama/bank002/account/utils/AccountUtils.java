package com.osama.bank002.account.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created";

    public static final String ACCOUNT_EXISTS_CODE = "003";
    public static final String ACCOUNT_EXISTS_MESSAGE = "User already has an account";

    public static final String ACCOUNT_NOT_EXISTS_CODE = "004";
    public static final String ACCOUNT_NOT_EXISTS_MESSAGE = "Account number does not exist";

    public static final String ACCOUNT_FOUND_CODE = "005";
    public static final String ACCOUNT_FOUND_SUCCESS = "Account found";

    public static final String ACCOUNT_CREDITED_SUCCESS_CODE = "006";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "Account credited successfully";

    public static final String ACCOUNT_DEBITED_SUCCESS_CODE = "007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account debited successfully";

    public static final String INSUFFICIENT_BALANCE_CODE = "008";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient balance";
    public static final String ACCOUNT_BLOCKED_CODE = "009";


    public static String generateAccountNumber(){
        /**
         * 2025 + randomSixDigits
         */
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        // Generate a random number between min and max
        int randNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);

        // convert the current year and random number to String thin concatenate it together
        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);
        StringBuilder accountNumber = new StringBuilder();

        return accountNumber.append(year).append(randomNumber).toString();
    }
}