package com.mk.todotasksh2.service;

import com.mk.todotasksh2.dto.UserCreateDto;
import com.mk.todotasksh2.dto.UserDto;
import com.mk.todotasksh2.dto.UserTasksDto;
import com.mk.todotasksh2.mapper.TasksMapper;
import com.mk.todotasksh2.mapper.UsersMapper;
import com.mk.todotasksh2.model.Role;
import com.mk.todotasksh2.model.Task;
import com.mk.todotasksh2.model.TaskState;
import com.mk.todotasksh2.model.User;
import com.mk.todotasksh2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UsersMapper usersMapper;

    @Mock
    private TasksMapper tasksMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void testFindAllUsers_withPagination_shouldReturnPagedUserDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        var user = new User();
        var userDto = new UserDto(1L, "user1@mail", Role.USER.name());

        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(user)));
        when(usersMapper.toListUserDto(any())).thenReturn(Collections.singletonList(userDto));

        var result = userService.findAllUsers(pageable);

        assertThat(result).contains(userDto).hasSize(1);
        verify(userRepository).findAll(pageable);
        verify(usersMapper).toListUserDto(any());
    }

    @Test
    void testFindUserTasks_withPagination_shouldReturnListUserTaskDtos() {
        var user = new User();
        user.setId(1L);
        var task = new Task();
        user.setTasks(Collections.singletonList(task));
        var userTasksDto = new UserTasksDto(1L,
                "task1",
                LocalDate.now(),
                TaskState.PLANNED,
                "user1@mail");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tasksMapper.toListUserTasksDto(any())).thenReturn(Collections.singletonList(userTasksDto));

        var result = userService.findUserTasks(1L);

        assertThat(result).contains(userTasksDto).hasSize(1);
        verify(userRepository).findById(1L);
        verify(tasksMapper).toListUserTasksDto(any());
    }

    @Test
    void testFindUserById_shouldReturnUserDto() {
        var user = new User();
        user.setId(1L);
        var userDto = new UserDto(1L, "user1@mail", Role.USER.name());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usersMapper.toUserDto(any())).thenReturn(userDto);

        var result = userService.findUserById(1L);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).findById(1L);
        verify(usersMapper).toUserDto(any());
    }

    @Test
    void testCreateUser_shouldReturnUserDto() {
        var userCreateDto = new UserCreateDto("user1@mail", "password");
        var user = new User();
        var userDto = new UserDto(1L, "user1@mail", Role.USER.name());

        when(userRepository.findByUsername("user1@mail")).thenReturn(Optional.empty());
        when(usersMapper.userCreateDtoToUser(any())).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(usersMapper.toUserDto(any())).thenReturn(userDto);

        var result = userService.createUser(userCreateDto);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).findByUsername("user1@mail");
        verify(usersMapper).userCreateDtoToUser(userCreateDto);
        verify(userRepository).save(user);
        verify(usersMapper).toUserDto(user);
    }

    @Test
    void testCreate_whenUserAlreadyExists_shouldThrowException() {
        var userCreateDto = new UserCreateDto("username", "password");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(new User()));

        var exception = assertThrows(ResponseStatusException.class,
                () -> userService.createUser(userCreateDto));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).isEqualTo("error.users.Username.message");
        verify(userRepository).findByUsername("username");
    }

    @Test
    void testChangeUserRole_shouldReturnUserDtoWithNewRole() {
        var user = new User();
        user.setId(1L);
        user.setRole(Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(usersMapper.toUserDto(any())).thenReturn(any());

        userService.changeUserRole(1L, Role.ADMIN);

        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(usersMapper).toUserDto(user);
    }

    @Test
    void testDeleteUser() {
        var user = new User();
        var task = new Task();
        task.setState(TaskState.WORK_IN_PROGRESS);
        user.setTasks(Collections.singletonList(task));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        assertThat(task.getState()).isEqualTo(TaskState.PLANNED);
        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void testLoadUserByUsername() {
        var user = new User();
        user.setUsername("user1@mail");

        when(userRepository.findByUsername("user1@mail")).thenReturn(Optional.of(user));

        var result = userService.loadUserByUsername("user1@mail");

        assertThat(result.getUsername()).isEqualTo("user1@mail");
        verify(userRepository).findByUsername("user1@mail");
    }

    @Test
    void testLoadUserByUsername_userNotFound_throwsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        var exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("unknownUser"));

        assertThat(exception.getMessage()).isEqualTo("Failed to retrieve user:unknownUser");
        verify(userRepository).findByUsername("unknownUser");
    }

    @Test
    void testFindById_userNotFound_throwsException() {
        var id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ResponseStatusException.class,
                () -> userService.findById(id));

        assertThat(exception.getReason()).isEqualTo("error.users.NotFound.message");
        verify(userRepository).findById(id);
    }

}