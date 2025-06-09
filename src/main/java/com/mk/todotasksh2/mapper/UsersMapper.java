package com.mk.todotasksh2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import com.mk.todotasksh2.dto.UserCreateDto;
import com.mk.todotasksh2.dto.UserDto;
import com.mk.todotasksh2.model.Role;
import com.mk.todotasksh2.model.User;
import com.mk.todotasksh2.util.UserMapperUtil;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                UserMapperUtil.class
        },
        imports = Role.class)
public interface UsersMapper {
    ;

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "userCreateDto.username")
    @Mapping(target = "role", expression = "java(Role.USER)")
    @Mapping(target = "password", qualifiedByName = {"UserMapperUtil", "getEncodePassword"})
    User userCreateDtoToUser(UserCreateDto userCreateDto);

    List<UserDto> toListUserDto(List<User> users);
}
