package com.osama.bank002.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank @Size(max = 60) String firstName,
        @NotBlank @Size(max = 60) String lastName,
        @Size(max = 60) String otherName,
        @NotBlank @Email @Size(max = 100) String email,
        @Size(max = 20) String gender,
        @NotBlank @Size(max = 120) String address,
        @NotBlank @Size(max = 60) String stateOfOrigin,
        @NotBlank @Size(max = 32) String phoneNumber,
        @Size(max = 32) String alternativePhoneNumber
) {
}