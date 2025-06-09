package com.mk.todotasksh2.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.mk.todotasksh2.model.TaskState;

import java.util.stream.Stream;

@Converter
public class TaskStateConverter implements AttributeConverter<TaskState, String> {
    @Override
    public String convertToDatabaseColumn(TaskState state) {
        if (state == null) {
            return null;
        }

        return Stream.of(TaskState.values())
                .filter(s -> s == state)
                .map(TaskState::getDbData)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown task state: " + state));

    }

    @Override
    public TaskState convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return Stream.of(TaskState.values())
                .filter(s->s.getDbData().equals(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown database value: " + dbData));
    }
}
