package com.mk.todotasksh2.controller;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.mk.todotasksh2.aop.LogExecutionTime;
import com.mk.todotasksh2.dto.TaskCreateDto;
import com.mk.todotasksh2.dto.TaskDto;
import com.mk.todotasksh2.dto.TaskUpdateDto;
import com.mk.todotasksh2.dto.UserDto;
import com.mk.todotasksh2.model.TaskState;
import com.mk.todotasksh2.service.TaskService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class TasksController {
    private final TaskService taskService;

    @GetMapping
    @Timed(value = "api.response.time", description = "Час відповіді findAllTasks")
    @Counted(value = "api.calls.count", description = "Кількість викликів findAllTasks")
    @LogExecutionTime
    @Operation(summary = "Get list of tasks.",
            description = "Return list of all tasks.",
            tags = "get")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", content = @Content())
    @ApiResponse(responseCode = "500", content = @Content())
    public ResponseEntity<List<TaskDto>> findAllTasks(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.findAllTasks(pageable));
    }

    @GetMapping("/{id}")
    @LogExecutionTime
    @Operation(summary = "Get task by id.",
            description = "Return task with the id passed in the path variable.",
            tags = "get")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", content = @Content())
    @ApiResponse(responseCode = "404", content = @Content())
    @ApiResponse(responseCode = "500", content = @Content())
    public ResponseEntity<TaskDto> findTaskById(@PathVariable("id") long id) {
        return ResponseEntity.ok(taskService.findTaskById(id));
    }

    @GetMapping("/{id}/users")
    @LogExecutionTime
    @Operation(summary = "Get task performer.",
            description = "Return the task performer. The task ID is passed in the path variable.",
            tags = "get")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", content = @Content())
    @ApiResponse(responseCode = "404", content = @Content())
    @ApiResponse(responseCode = "500", content = @Content())
    public ResponseEntity<UserDto> findTaskPerformer(@PathVariable("id") long id) {
        return ResponseEntity.ok(taskService.findTaskPerformer(id));
    }

    @PostMapping
    @LogExecutionTime
    @Operation(summary = "Create new task(for admin only).",
            description = "Return created task.",
            tags = "post")
    @ApiResponse(responseCode = "201",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", content = @Content())
    @ApiResponse(responseCode = "403", content = @Content())
    @ApiResponse(responseCode = "404", content = @Content())
    @ApiResponse(responseCode = "500", content = @Content())
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskCreateDto taskCreateDto) {
        return new ResponseEntity<>(taskService.createTask(taskCreateDto), HttpStatus.CREATED);

    }

    @PatchMapping("/{id}")
    @LogExecutionTime
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Change task info.",
            description = "Returns edited task with the new info passed in the request body. " +
                    "The user ID is passed in the path variable.",
            tags = "path")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", content = @Content())
    @ApiResponse(responseCode = "404", content = @Content())
    @ApiResponse(responseCode = "500", content = @Content())
    public ResponseEntity<TaskDto> editTask(@PathVariable Long id, @RequestBody @Valid TaskUpdateDto taskUpdateDto) {
        return ResponseEntity.ok(taskService.editTask(id, taskUpdateDto));
    }

    @PatchMapping("/{id}/state")
    @LogExecutionTime
    @Operation(summary = "Change task state.",
            description = "Returns edited task with the new state passed in the request body. " +
                    "The user ID is passed in the path variable.",
            tags = "path")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", content = @Content())
    @ApiResponse(responseCode = "403", content = @Content())
    @ApiResponse(responseCode = "404", content = @Content())
    @ApiResponse(responseCode = "500", content = @Content())
    public ResponseEntity<TaskDto> editState(@PathVariable Long id, @RequestBody TaskState state) {
        return ResponseEntity.ok(taskService.changeState(id, state));
    }


    @PatchMapping("/{taskId}/users/{userId}")
    @LogExecutionTime
    @Operation(summary = "Assigns or changes the task performer.",
            description = "Returns edited task with the new user. " +
                    "Task ID and user ID are passed in the path variables.",
            tags = "path")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", content = @Content())
    @ApiResponse(responseCode = "403", content = @Content())
    @ApiResponse(responseCode = "404", content = @Content())
    @ApiResponse(responseCode = "500", content = @Content())
    public ResponseEntity<TaskDto> assignUserToTask(@PathVariable Long taskId, @PathVariable Long userId) {
        return ResponseEntity.ok(taskService.assignUserToTask(taskId, userId));
    }

    @DeleteMapping("/{id}")
    @LogExecutionTime
    @Operation(summary = "Delete task by id(for admin only).",
            description = "Delete task with the id passed in the path variable",
            tags = "delete")
    @ApiResponse(responseCode = "204", description = "Person successfully deleted")
    @ApiResponse(responseCode = "400", description = "Invalid input Id", content = @Content)
    @ApiResponse(responseCode = "403", content = @Content())
    @ApiResponse(responseCode = "404", content = @Content)
    @ApiResponse(responseCode = "500", content = @Content)
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/users")
    @LogExecutionTime
    @Operation(summary = "Delete user from task(for admin only).",
            description = "Delete user from task with the id passed in the path variable",
            tags = "delete")
    @ApiResponse(responseCode = "204", description = "Person successfully deleted")
    @ApiResponse(responseCode = "400", description = "Invalid input Id", content = @Content)
    @ApiResponse(responseCode = "403", content = @Content())
    @ApiResponse(responseCode = "404", content = @Content)
    @ApiResponse(responseCode = "500", content = @Content)
    public ResponseEntity<TaskDto> deleteUserFromTask(@PathVariable long id) {
        return ResponseEntity.ok(taskService.deleteUserFromTask(id));
    }
}
