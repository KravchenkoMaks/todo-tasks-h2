package com.mk.todotasksh2.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER('u'),
    ADMIN('a');

    private final char dbData;

    Role(char dbData) {
        this.dbData = dbData;
    }

    public char getDbData() {
        return dbData;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}

