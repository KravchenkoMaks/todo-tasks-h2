package com.mk.todotasksh2.mapper;

import com.mk.todotasksh2.dto.UserCreateDto;
import com.mk.todotasksh2.dto.UserDto;
import com.mk.todotasksh2.model.Role;
import com.mk.todotasksh2.model.User;
import com.mk.todotasksh2.util.UserMapperUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersMapperTest {
    @Mock
    private UserMapperUtil userMapperUtil;

    @InjectMocks
    private final UsersMapper usersMapper = Mappers.getMapper(UsersMapper.class);

    @Test
    void testUserCreateDtoToUser() {
        UserCreateDto userCreateDto = new UserCreateDto("testUser", "password");
        when(userMapperUtil.encodePassword(anyString())).thenReturn("encodedPassword");

        User user = usersMapper.userCreateDtoToUser(userCreateDto);

        assertThat(user.getUsername()).isEqualTo("testUser");
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getId()).isNull();
    }

    @Test
    void testToUserDto() {
        User user = User.builder()
                .id(1L)
                .username("testUser")
                .role(Role.USER)
                .build();
        UserDto userDto = usersMapper.toUserDto(user);

        assertThat(userDto.id()).isEqualTo(1L);
        assertThat(userDto.username()).isEqualTo("testUser");
        assertThat(userDto.role()).isEqualTo(Role.USER.name());

    }

    @Test
    void testToListUserDto() {
        User user1 = User.builder()
                .id(1L)
                .username("user1")
                .build();

        User user2 = User.builder()
                .id(1L)
                .username("user2")
                .build();

        List<User> users = List.of(user1, user2);

        List<UserDto> userDtos = usersMapper.toListUserDto(users);

        assertThat(userDtos).hasSize(2);
        assertThat(userDtos.get(0).username()).isEqualTo("user1");
        assertThat(userDtos.get(1).username()).isEqualTo("user2");
    }
}