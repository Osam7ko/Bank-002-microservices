package com.osama.bank002.card.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCardStatusRequest(@NotBlank String status) {
}