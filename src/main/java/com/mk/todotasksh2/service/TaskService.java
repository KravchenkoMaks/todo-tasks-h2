package com.mk.todotasksh2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.mk.todotasksh2.dto.TaskCreateDto;
import com.mk.todotasksh2.dto.TaskDto;
import com.mk.todotasksh2.dto.TaskUpdateDto;
import com.mk.todotasksh2.dto.UserDto;
import com.mk.todotasksh2.mapper.TasksMapper;
import com.mk.todotasksh2.mapper.UsersMapper;
import com.mk.todotasksh2.model.Role;
import com.mk.todotasksh2.model.Task;
import com.mk.todotasksh2.model.TaskState;
import com.mk.todotasksh2.model.User;
import com.mk.todotasksh2.repository.TasksRepository;
import com.mk.todotasksh2.sequrity.AppUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TasksRepository tasksRepository;

    private final UserService userService;

    private final TasksMapper tasksMapper;

    private final UsersMapper usersMapper;

    public List<TaskDto> findAllTasks(Pageable pageable) {
        return tasksMapper.toListTasksDto(tasksRepository.findAll(pageable).getContent());
    }

    public UserDto findTaskPerformer(long id) {
        User user = findById(id).getUser();
        if (user == null) {
            log.warn("No user assigned to task ID: {}", id);
            user = new User();
            user.setUsername("unassigned");
        }
        return usersMapper.toUserDto(user);
    }

    public TaskDto findTaskById(long id) {
        return tasksMapper.toTaskDto(findById(id));
    }

    public TaskDto createTask(TaskCreateDto taskCreateDto) {
        Task task = tasksMapper.taskCreateDtoToTask(taskCreateDto);
        Task savedTask = tasksRepository.save(task);
        return tasksMapper.toTaskDto(savedTask);
    }

    public TaskDto changeState(Long taskId, TaskState newState) {
        Task task = findById(taskId);
        TaskState currentState = task.getState();
        if (currentState == newState ) {
            return tasksMapper.toTaskDto(task);
        }
        if (isNotAdmin() && (newState == TaskState.DONE || newState == TaskState.CANCELLED)) {
            log.error("User trying to reassign task ID: {} which is not allowed", taskId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "error.NotAccessChangeState.message");
        }
        task.setState(TaskState.changeState(currentState, newState));
        tasksRepository.save(task);

        return tasksMapper.toTaskDto(task);
    }

    public TaskDto assignUserToTask(Long taskId, Long userId) {
        Task task = findById(taskId);
        User taskUser = task.getUser();

        if (taskUser != null && isNotAdmin()) {
            log.error("User trying to reassign task ID: {} which is not allowed", taskId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "error.AccessDenied.message");
        }

        User newUser = userService.findById(userId);
        newUser.addTask(task);
        tasksRepository.save(task);

        return tasksMapper.toTaskDto(task);
    }

    private boolean isNotAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();
        return Role.ADMIN != principal.getUser().getRole();
    }


    public void deleteTaskById(Long id) {
        if (tasksRepository.existsById(id)) {
            tasksRepository.deleteById(id);
            log.debug("Task ID: {} successfully deleted", id);
        } else {
            log.error("Task ID: {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error.tasks.NotFound.message");
        }
    }

    private Task findById(long id) {
        return tasksRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task ID: {} not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "error.tasks.NotFound.message");
                });
    }

    public TaskDto deleteUserFromTask(long id) {
        Task task = findById(id);
        task.setUser(null);
        tasksRepository.save(task);
        return tasksMapper.toTaskDto(task);
    }

    public TaskDto editTask(Long id, TaskUpdateDto taskUpdateDto) {
        Task task = findById(id);
        tasksMapper.updateTaskFromDto(taskUpdateDto, task);
        tasksRepository.save(task);
        return tasksMapper.toTaskDto(task);
    }
}
