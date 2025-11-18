package com.mk.todotasksh2.exeption;

import com.mk.todotasksh2.model.TaskState;
import lombok.Getter;

import java.util.Set;

@Getter
public class NotChangeStatusException extends RuntimeException {

    private final String bundle;
    private final Set<TaskState> availableStates;

    public NotChangeStatusException(String bundle, Set<TaskState> availableStates) {
        this.bundle = bundle;
        this.availableStates = availableStates;
    }

}
