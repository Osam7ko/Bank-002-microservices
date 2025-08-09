package com.osama.bank002.account.client.dto;

public record ProfileSummary(String id,String userId,String firstName,String lastName,String email) {

    public String fullName(){
        String fn = firstName == null ? "" : firstName.trim();
        String ln = lastName == null ? "" : lastName.trim();
        return (fn + " " + ln).trim();
    }
}