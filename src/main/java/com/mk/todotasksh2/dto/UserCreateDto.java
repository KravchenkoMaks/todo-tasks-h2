package com.mk.todotasksh2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateDto(
        @Email
        @NotBlank
        String username,
        @NotBlank
        String password) {
}
