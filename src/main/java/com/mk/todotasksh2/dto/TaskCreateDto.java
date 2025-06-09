package com.mk.todotasksh2.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskCreateDto(
        @NotBlank
        String description,
        @NotNull
        @FutureOrPresent
        LocalDate deadline) {
}
