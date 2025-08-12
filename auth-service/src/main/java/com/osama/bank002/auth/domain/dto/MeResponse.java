package com.osama.bank002.auth.domain.dto;

import java.util.List;

public record MeResponse(Long id, String email, List<String> roles) {
}