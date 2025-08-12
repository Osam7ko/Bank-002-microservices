package com.osama.bank002.auth.domain.dto;

public record RegisterRequest(String email, String password, String firstName, String lastName) {
}
