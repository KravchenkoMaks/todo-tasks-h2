package com.mk.todotasksh2.model;

import lombok.extern.slf4j.Slf4j;
import com.mk.todotasksh2.exeption.NotChangeStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;
import java.util.Set;

@Slf4j
public enum TaskState {
    PLANNED("pl", null),
    WORK_IN_PROGRESS("wp", null),
    POSTPONED("pp",null ),
    NOTIFIED("nf", null),
    SIGNED("sn", null),
    DONE("dn", null),
    CANCELLED("cn", null);

    private final String dbData;
    private Set<TaskState> availableStates;


    TaskState(String dbData, Set<TaskState> availableStates) {
        this.dbData = dbData;
        this.availableStates = availableStates;
    }

    static {
        PLANNED.availableStates = EnumSet.of(WORK_IN_PROGRESS, POSTPONED, CANCELLED);
        WORK_IN_PROGRESS.availableStates = EnumSet.of(WORK_IN_PROGRESS, NOTIFIED, SIGNED, CANCELLED);
        POSTPONED.availableStates = EnumSet.of(POSTPONED, NOTIFIED, SIGNED, CANCELLED);
        NOTIFIED.availableStates = EnumSet.of(SIGNED, DONE, CANCELLED);
        SIGNED.availableStates = EnumSet.of(NOTIFIED, DONE, CANCELLED);
        DONE.availableStates = EnumSet.noneOf(TaskState.class);
        CANCELLED.availableStates = EnumSet.noneOf(TaskState.class);
    }

    public static TaskState changeState(TaskState state, TaskState newState) {
        if (state.availableStates.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error.tasks.EndState.message");
        }
        if (state.availableStates.contains(newState)) {
            return newState;
        }
        log.error("Incorrect change of {} state to {}", state, newState);
        throw new NotChangeStatusException("error.tasks.State.message", state.availableStates);
    }

    public String getDbData() {
        return dbData;
    }
}
