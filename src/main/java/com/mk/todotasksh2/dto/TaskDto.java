package com.mk.todotasksh2.dto;



import com.mk.todotasksh2.model.TaskState;

import java.time.LocalDate;

public record TaskDto(long id, String description, LocalDate deadline, TaskState state, UserDto user) {
}
