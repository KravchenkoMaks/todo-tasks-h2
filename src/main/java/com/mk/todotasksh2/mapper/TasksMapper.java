package com.mk.todotasksh2.mapper;

import org.mapstruct.*;
import com.mk.todotasksh2.dto.TaskCreateDto;
import com.mk.todotasksh2.dto.TaskDto;
import com.mk.todotasksh2.dto.TaskUpdateDto;
import com.mk.todotasksh2.dto.UserTasksDto;
import com.mk.todotasksh2.model.Task;
import com.mk.todotasksh2.model.TaskState;

import java.util.List;

@Mapper(componentModel =MappingConstants.ComponentModel.SPRING, imports = {UsersMapper.class})
public interface TasksMapper {

    TaskDto toTaskDto(Task task);

    List<TaskDto> toListTasksDto(List<Task> tasks);

    @Mapping(target = "username", source = "user.username")
    UserTasksDto taskToUserTasksDto(Task task);

    List<UserTasksDto> toListUserTasksDto(List<Task> tasks);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", expression = "java(getState())")
    @Mapping(target = "deadline", source = "taskCreateDto.deadline")
    Task taskCreateDtoToTask(TaskCreateDto taskCreateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTaskFromDto(TaskUpdateDto taskUpdateDto, @MappingTarget Task taskToBeUpdated);

    default TaskState getState() {
        return TaskState.PLANNED;
    }
}
