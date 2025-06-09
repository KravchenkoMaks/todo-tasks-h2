package com.mk.todotasksh2.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static com.mk.todotasksh2.model.TaskState.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    public static final Task TASK_1 = new Task(1L, "Task 1", LocalDate.now(), PLANNED, new User());
    public static final Task TASK_2 = new Task(2L, "Task 2", LocalDate.now(), WORK_IN_PROGRESS, new User());
    public static final TaskDto TASK_DTO = new TaskDto(1L, "Task 1", LocalDate.now(), PLANNED, new UserDto(1L, "", ""));
    @Mock
    private TasksRepository tasksRepository;

    @Mock
    private TasksMapper tasksMapper;

    @Mock
    private UsersMapper usersMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private AppUserDetails appUserDetails;


    @BeforeEach
    void init() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void findAllTasks_withPagination_shouldReturnPagedTaskDtos() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Task> tasks = Arrays.asList(TASK_1, TASK_2);

        Page<Task> pagedTasks = new PageImpl<>(tasks, pageable, tasks.size());
        List<TaskDto> taskDtos = Arrays.asList(
                new TaskDto(1L, "Task 1", LocalDate.now(), PLANNED, new UserDto(1L, "", "")),
                new TaskDto(2L, "Task 2", LocalDate.now(), WORK_IN_PROGRESS, new UserDto(1L, "", "")));

        when(tasksRepository.findAll(pageable)).thenReturn(pagedTasks);
        when(tasksMapper.toListTasksDto(tasks)).thenReturn(taskDtos);

        List<TaskDto> result = taskService.findAllTasks(pageable);

        assertEquals(2, result.size());
        verify(tasksRepository, times(1)).findAll(pageable);
        verify(tasksMapper, times(1)).toListTasksDto(tasks);
    }

    @Test
    void findTaskOwner_shouldReturnUserDto() {
        User user = new User(1L, "John", "123", Role.USER, Collections.emptyList());
        Task task = new Task(1L, "Task 1", LocalDate.now(), PLANNED, user);
        UserDto userDto = new UserDto(1L, "John", "ROLE_USER");

        when(tasksRepository.findById(1L)).thenReturn(Optional.of(task));
        when(usersMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = taskService.findTaskPerformer(1L);

        assertEquals("John", result.username());
        verify(tasksRepository, times(1)).findById(1L);
        verify(usersMapper, times(1)).toUserDto(user);
    }

    @Test
    void findTaskById_shouldReturnTaskDto() {
        long taskId = 1L;

        when(tasksRepository.findById(taskId)).thenReturn(Optional.of(TASK_1));
        when(tasksMapper.toTaskDto(TASK_1)).thenReturn(TASK_DTO);

        TaskDto result = taskService.findTaskById(taskId);

        assertNotNull(result);
        assertEquals(TASK_DTO.id(), result.id());
        assertEquals(TASK_DTO.description(), result.description());
        verify(tasksRepository, times(1)).findById(taskId);
        verify(tasksMapper, times(1)).toTaskDto(TASK_1);
    }

    @Test
    void findTaskById_whenTaskNotFound_shouldThrowException() {
        long taskId = 1L;
        when(tasksRepository.findById(taskId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.findTaskById(taskId));

        assertEquals("404 NOT_FOUND \"error.tasks.NotFound.message\"", exception.getMessage());
        verify(tasksRepository, times(1)).findById(taskId);
        verify(tasksMapper, times(0)).toTaskDto(any());
    }

    @Test
    void createTask_shouldReturnCreatedTaskDto() {
        TaskCreateDto taskCreateDto = new TaskCreateDto("New Task", LocalDate.now());
        TaskDto taskDto = new TaskDto(1L, "New Task", LocalDate.of(2024, 10, 10), PLANNED, null);

        when(tasksMapper.taskCreateDtoToTask(taskCreateDto)).thenReturn(TASK_1);
        when(tasksRepository.save(TASK_1)).thenReturn(TASK_1);
        when(tasksMapper.toTaskDto(TASK_1)).thenReturn(taskDto);

        TaskDto result = taskService.createTask(taskCreateDto);

        assertEquals("New Task", result.description());
        verify(tasksMapper, times(1)).taskCreateDtoToTask(taskCreateDto);
        verify(tasksRepository, times(1)).save(TASK_1);
        verify(tasksMapper, times(1)).toTaskDto(TASK_1);
    }

    @Test
    void deleteTaskById_taskExists_deletesTask() {
        long taskId = 1L;
        when(tasksRepository.existsById(taskId)).thenReturn(true);

        taskService.deleteTaskById(taskId);

        verify(tasksRepository).deleteById(taskId);
    }

    @Test
    void deleteTaskById_taskNotFound_throwsException() {
        long taskId = 1L;
        when(tasksRepository.existsById(taskId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.deleteTaskById(taskId));

        assertEquals("error.tasks.NotFound.message", exception.getReason());
        verify(tasksRepository, never()).deleteById(taskId);
    }

    @Test
    void deleteUserFromTask_taskExists_userDeleted() {
        long taskId = 1L;

        when(tasksRepository.findById(taskId)).thenReturn(Optional.of(TASK_1));
        when(tasksMapper.toTaskDto(TASK_1)).thenReturn(TASK_DTO);

        TaskDto result = taskService.deleteUserFromTask(taskId);

        assertNotNull(result);
        assertNull(TASK_1.getUser());
        assertEquals(TASK_DTO, result);
        verify(tasksRepository).save(TASK_1);
    }

    @Test
    void deleteUserFromTask_taskNotFound_throwsException() {
        long taskId = 1L;
        when(tasksRepository.findById(taskId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                taskService.deleteUserFromTask(taskId));

        assertEquals("error.tasks.NotFound.message", exception.getReason());
        verify(tasksRepository, never()).save(any());
    }

    @Test
    void editTask_taskExists_taskUpdated() {
        long taskId = 1L;
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto("New description", LocalDate.now());

        when(tasksRepository.findById(taskId)).thenReturn(Optional.of(TASK_1));
        doNothing().when(tasksMapper).updateTaskFromDto(taskUpdateDto, TASK_1);
        when(tasksMapper.toTaskDto(TASK_1)).thenReturn(TASK_DTO);

        TaskDto result = taskService.editTask(taskId, taskUpdateDto);

        assertNotNull(result);
        assertEquals(TASK_DTO, result);
        verify(tasksMapper).updateTaskFromDto(taskUpdateDto, TASK_1);
        verify(tasksRepository).save(TASK_1);
    }

    @Test
    void editTask_taskNotFound_throwsException() {
        long taskId = 1L;
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto("New description", LocalDate.now());
        when(tasksRepository.findById(taskId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.editTask(taskId, taskUpdateDto));

        assertEquals("error.tasks.NotFound.message", exception.getReason());
        verify(tasksRepository, never()).save(any());
    }

    @Test
    void assignUserToTask_taskNotFound_throwsException() {
        long taskId = 1L;
        long userId = 2L;

        when(tasksRepository.findById(taskId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.assignUserToTask(taskId, userId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(userService, never()).findById(anyLong());
        verify(tasksRepository, never()).save(any());
    }

    @Test
    void assignUserToTask_userNotFound_throwsException() {
        long taskId = 1L;
        long userId = 2L;

        Task task = new Task();
        when(tasksRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userService.findById(userId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.assignUserToTask(taskId, userId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(tasksRepository, never()).save(any());
    }

    @Test
    void whenTaskHasUserAndCurrentUserIsNotAdmin_thenThrowForbidden() {
        long taskId = 1L;
        long userId = 2L;

        when(tasksRepository.findById(taskId)).thenReturn(Optional.of(TASK_1));
        when(authentication.getPrincipal()).thenReturn(appUserDetails);
        when(appUserDetails.getUser()).thenReturn(createUserWithRole(Role.USER));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.assignUserToTask(taskId, userId));

        assertEquals("error.AccessDenied.message", exception.getReason());
        verify(tasksRepository, never()).save(any());

    }

    @Test
    void whenTaskHasUserAndCurrentUserIsAdmin_thenReassignUser() {
        long taskId = 1L;
        long userId = 2L;
        var user = createUserWithRole(Role.ADMIN);
        var newUserTasks = new ArrayList<Task>();
        var newUser = new User(3L, "user_3@mail", "123", Role.USER, newUserTasks);

        Task task = new Task(taskId, "Task 1", LocalDate.of(2024, 10, 10), PLANNED, user);

        when(tasksRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(authentication.getPrincipal()).thenReturn(appUserDetails);
        when(appUserDetails.getUser()).thenReturn(createUserWithRole(Role.ADMIN));


        when(userService.findById(2L)).thenReturn(newUser);
        when(tasksRepository.save(task)).thenReturn(task);

        taskService.assignUserToTask(taskId, userId);

        verify(tasksRepository, times(1)).save(task);
        verify(tasksMapper, times(1)).toTaskDto(task);
        assertEquals(task.getUser(), newUser);
        assertEquals(newUser.getTasks().getFirst(), task);
    }

    @Test
    void editState_PlannedToWorkInProgress_shouldReturnUpdatedTaskDto() {
        TaskState newState = WORK_IN_PROGRESS;
        Task updatedTask = new Task(1L, "Task 1", LocalDate.of(2024, 10, 10), newState, null);
        TaskDto updatedTaskDto = new TaskDto(1L, "Task 1", LocalDate.of(2024, 10, 10), newState, null);

        when(authentication.getPrincipal()).thenReturn(appUserDetails);
        when(appUserDetails.getUser()).thenReturn(createUserWithRole(Role.USER));

        when(tasksRepository.findById(1L)).thenReturn(Optional.of(TASK_1));
        when(tasksRepository.save(TASK_1)).thenReturn(updatedTask);
        when(tasksMapper.toTaskDto(updatedTask)).thenReturn(updatedTaskDto);

        TaskDto result = taskService.changeState(1L, WORK_IN_PROGRESS);

        assertEquals(WORK_IN_PROGRESS, TASK_1.getState());
        assertEquals(WORK_IN_PROGRESS, result.state());
        verify(tasksRepository, times(1)).findById(1L);
        verify(tasksRepository, times(1)).save(TASK_1);
        verify(tasksMapper, times(1)).toTaskDto(updatedTask);
    }

    @Test
    void editState_WorkInProgressToNotified_shouldReturnUpdatedTaskDto() {
        Task task = new Task(1L, "Task 1", LocalDate.of(2024, 10, 10), WORK_IN_PROGRESS, null);
        TaskState newState = NOTIFIED;
        Task updatedTask = new Task(1L, "Task 1", LocalDate.of(2024, 10, 10), newState, null);
        TaskDto updatedTaskDto = new TaskDto(1L, "Task 1", LocalDate.of(2024, 10, 10), newState, null);

        when(authentication.getPrincipal()).thenReturn(appUserDetails);
        when(appUserDetails.getUser()).thenReturn(createUserWithRole(Role.ADMIN));

        when(tasksRepository.findById(1L)).thenReturn(Optional.of(task));
        when(tasksRepository.save(task)).thenReturn(updatedTask);
        when(tasksMapper.toTaskDto(updatedTask)).thenReturn(updatedTaskDto);

        TaskDto result = taskService.changeState(1L, NOTIFIED);

        assertEquals(NOTIFIED, task.getState());
        assertEquals(NOTIFIED, result.state());
        verify(tasksRepository, times(1)).findById(1L);
        verify(tasksRepository, times(1)).save(task);
        verify(tasksMapper, times(1)).toTaskDto(updatedTask);
    }

    @Test
    void editState_NotifiedToDone_shouldReturnUpdatedTaskDto() {
        Task task = new Task(1L, "Task 1", LocalDate.of(2024, 10, 10), NOTIFIED, null);
        TaskState newState = DONE;
        Task updatedTask = new Task(1L, "Task 1", LocalDate.of(2024, 10, 10), newState, null);
        TaskDto updatedTaskDto = new TaskDto(1L, "Task 1", LocalDate.of(2024, 10, 10), newState, null);

        when(authentication.getPrincipal()).thenReturn(appUserDetails);
        when(appUserDetails.getUser()).thenReturn(createUserWithRole(Role.ADMIN));

        when(tasksRepository.findById(1L)).thenReturn(Optional.of(task));
        when(tasksRepository.save(task)).thenReturn(updatedTask);
        when(tasksMapper.toTaskDto(updatedTask)).thenReturn(updatedTaskDto);

        TaskDto result = taskService.changeState(1L, DONE);

        assertEquals(DONE, task.getState());
        assertEquals(DONE, result.state());
        verify(tasksRepository, times(1)).findById(1L);
        verify(tasksRepository, times(1)).save(task);
        verify(tasksMapper, times(1)).toTaskDto(updatedTask);
    }

    @Test
    void editState_invalidTransition_ifNotAdmin_shouldThrowException() {
        when(tasksRepository.findById(1L)).thenReturn(Optional.of(TASK_1));

        when(authentication.getPrincipal()).thenReturn(appUserDetails);
        when(appUserDetails.getUser()).thenReturn(createUserWithRole(Role.USER));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                taskService.changeState(1L, DONE));

        assertEquals("error.NotAccessChangeState.message", exception.getReason());
        verify(tasksRepository, times(1)).findById(1L);
    }

    @Test
    void whenUserTriesToSetStateToDoneOrCancelled_thenThrowForbidden() {
        when(authentication.getPrincipal()).thenReturn(appUserDetails);
        when(appUserDetails.getUser()).thenReturn(createUserWithRole(Role.USER));
        when(tasksRepository.findById(1L)).thenReturn(Optional.of(TASK_1));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                taskService.changeState(1L, DONE));

        assertEquals("error.NotAccessChangeState.message", exception.getReason());
        verify(tasksRepository, times(1)).findById(1L);
    }

    private User createUserWithRole(Role role) {
        User user = new User();
        user.setRole(role);
        return user;
    }
}
