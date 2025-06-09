package com.mk.todotasksh2.util;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Named("UserMapperUtil")
@Component
@RequiredArgsConstructor
public class UserMapperUtil {

    private final PasswordEncoder passwordEncoder;

    @Named("getEncodePassword")
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
