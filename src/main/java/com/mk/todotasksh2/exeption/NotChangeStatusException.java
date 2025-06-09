package com.mk.todotasksh2.exeption;

import com.mk.todotasksh2.model.TaskState;

import java.util.Set;

public class NotChangeStatusException extends RuntimeException {

    private final String bundle;
    private final Set<TaskState> availableStates;

    public NotChangeStatusException(String bundle, Set<TaskState> availableStates) {
        this.bundle = bundle;
        this.availableStates = availableStates;
    }

    public String getBundle() {
        return bundle;
    }

    public Set<TaskState> getAvailableStates() {
        return availableStates;
    }
}
