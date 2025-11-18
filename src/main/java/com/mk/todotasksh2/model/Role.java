package com.mk.todotasksh2.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role implements GrantedAuthority {
    USER('u'),
    ADMIN('a');

    private final char dbData;

    Role(char dbData) {
        this.dbData = dbData;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}

