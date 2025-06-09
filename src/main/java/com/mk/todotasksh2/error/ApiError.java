package com.mk.todotasksh2.error;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        List<String> message,
        String path) {
}
