package com.mk.todotasksh2.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperUtilTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserMapperUtil userMapperUtil;

    @Test
    void testEncodePassword() {
        String rawPassword = "password123";
        when(passwordEncoder.encode(rawPassword)).thenReturn("encodedPassword123");

        assertThat(userMapperUtil.encodePassword(rawPassword)).isEqualTo("encodedPassword123");
    }

}