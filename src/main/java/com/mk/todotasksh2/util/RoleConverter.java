package com.mk.todotasksh2.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.mk.todotasksh2.model.Role;

import java.util.stream.Stream;

@Converter
public class RoleConverter implements AttributeConverter<Role, Character> {
    @Override
    public Character convertToDatabaseColumn(Role role) {
        if (role == null) {
            return null;
        }
        return Stream.of(Role.values())
                .filter(r -> r == role)
                .map(Role::getDbData)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + role));
    }

    @Override
    public Role convertToEntityAttribute(Character dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(Role.values())
                .filter(role -> role.getDbData() == dbData)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown database value: " + dbData));
    }
}
