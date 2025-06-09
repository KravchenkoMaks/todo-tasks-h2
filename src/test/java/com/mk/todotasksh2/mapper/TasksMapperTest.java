package com.mk.todotasksh2.mapper;


import com.mk.todotasksh2.dto.TaskCreateDto;
import com.mk.todotasksh2.dto.TaskDto;
import com.mk.todotasksh2.dto.TaskUpdateDto;
import com.mk.todotasksh2.dto.UserTasksDto;
import com.mk.todotasksh2.model.Task;
import com.mk.todotasksh2.model.TaskState;
import com.mk.todotasksh2.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TasksMapperTest {

    @InjectMocks
    private final TasksMapper tasksMapper = Mappers.getMapper(TasksMapper.class);

    @Test
    void testTaskCreateDtoToTask() {

        TaskCreateDto taskCreateDto = new TaskCreateDto("taskTest", LocalDate.now());

        Task task = tasksMapper.taskCreateDtoToTask(taskCreateDto);

        assertThat(task.getId()).isNull();
        assertThat(task.getState()).isEqualTo(TaskState.PLANNED);
        assertThat(task.getDeadline()).isEqualTo(LocalDate.now());
    }

    @Test
    void testTaskToTaskDto() {
        Task task = Task.builder()
                .id(1L)
                .state(TaskState.WORK_IN_PROGRESS)
                .build();

        TaskDto taskDto = tasksMapper.toTaskDto(task);

        assertThat(taskDto.id()).isEqualTo(1L);
        assertThat(taskDto.state()).isEqualTo(TaskState.WORK_IN_PROGRESS);
    }

    @Test
    void testUpdateTaskFromDto() {
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto("taskTest", LocalDate.now());

        Task taskToBeUpdated = Task.builder()
                .state(TaskState.WORK_IN_PROGRESS)
                .build();

        tasksMapper.updateTaskFromDto(taskUpdateDto, taskToBeUpdated);

        assertThat(taskToBeUpdated.getState()).isEqualTo(TaskState.WORK_IN_PROGRESS);
        assertThat(taskToBeUpdated.getDeadline()).isEqualTo(LocalDate.now());
    }

    @Test
    void testTaskToUserTasksDto() {
        Task task = new Task();
        User user = User.builder()
                .username("testUser")
                .build();

        task.setUser(user);

        UserTasksDto userTasksDto = tasksMapper.taskToUserTasksDto(task);

        assertThat(userTasksDto.username()).isEqualTo("testUser");
    }

    @Test
    void testToListTasksDto() {
        Task task1 = Task.builder().id(1L).build();
        Task task2 = Task.builder().id(2L).build();

        List<Task> tasks = List.of(task1, task2);

        List<TaskDto> taskDtos = tasksMapper.toListTasksDto(tasks);

        assertThat(taskDtos).hasSize(2);
        assertThat(taskDtos.get(0).id()).isEqualTo(1L);
        assertThat(taskDtos.get(1).id()).isEqualTo(2L);
    }
}