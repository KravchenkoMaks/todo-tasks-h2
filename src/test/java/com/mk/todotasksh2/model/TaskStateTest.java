package com.mk.todotasksh2.model;

import com.mk.todotasksh2.exeption.NotChangeStatusException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskStateTest {

    @Test
    void testValidStateChange() {
        TaskState currentState = TaskState.PLANNED;
        TaskState newState = TaskState.WORK_IN_PROGRESS;

        TaskState result = TaskState.changeState(currentState, newState);

        assertThat(result).isEqualTo(newState);
    }

    @Test
    void testInvalidStateChange() {
        TaskState currentState = TaskState.SIGNED;
        TaskState newState = TaskState.WORK_IN_PROGRESS;

        NotChangeStatusException exception = assertThrows(NotChangeStatusException.class,
                () -> TaskState.changeState(currentState, newState));

        assertThat(exception.getBundle()).isEqualTo("error.tasks.State.message");
    }
}