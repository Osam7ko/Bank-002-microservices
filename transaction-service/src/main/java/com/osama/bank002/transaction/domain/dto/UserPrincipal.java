package com.osama.bank002.transaction.domain.dto;

import java.util.List;

public record UserPrincipal(String userId, List<String> roles, String token) {
}