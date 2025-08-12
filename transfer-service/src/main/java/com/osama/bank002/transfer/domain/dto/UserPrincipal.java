package com.osama.bank002.transfer.domain.dto;

import java.util.List;

public record UserPrincipal(String userId, List<String> roles, String token) {
}