package com.mk.todotasksh2.dto;

import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

public record TaskUpdateDto(
        String description,
        @FutureOrPresent
        LocalDate deadline) {
}
