package com.mk.todotasksh2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import com.mk.todotasksh2.sequrity.AppUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UsersMapper usersMapper;

    private final TasksMapper tasksMapper;

    public List<UserDto> findAllUsers(Pageable pageable) {
        return usersMapper.toListUserDto(userRepository.findAll(pageable).getContent());
    }

    public List<UserTasksDto> findUserTasks(long id) {
        return tasksMapper.toListUserTasksDto(findById(id).getTasks());
    }

    public UserDto findUserById(long id) {
        return usersMapper.toUserDto(findById(id));
    }

    public UserDto createUser(UserCreateDto userCreateDto) {
        if (userRepository.findByUsername(userCreateDto.username()).isPresent()) {
            log.error("Username {} already exists", userCreateDto.username());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error.users.Username.message");
        }
        User user = usersMapper.userCreateDtoToUser(userCreateDto);
        User savedUser = userRepository.save(user);

        return usersMapper.toUserDto(savedUser);
    }

    public UserDto changeUserRole(Long id, Role role) {
        User user = findById(id);
        if (user.getRole() != role) {
            user.setRole(role);
            userRepository.save(user);
        }
        return usersMapper.toUserDto(user);
    }

    public void deleteUser(Long id) {
        User user = findById(id);
        List<Task> tasks = user.getTasks();
        installDefaultTaskState(tasks);
        userRepository.delete(user);
    }

    private static void installDefaultTaskState(List<Task> tasks) {
        for (Task task : tasks) {
            var state = task.getState();
            if (!(state == TaskState.DONE || state == TaskState.CANCELLED)) {
                task.setState(TaskState.PLANNED);
            }
        }
    }

    protected User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User ID: {} not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "error.users.NotFound.message");
                });
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new AppUserDetails(user))
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UsernameNotFoundException("Failed to retrieve user:" + username);
                });
    }
}
