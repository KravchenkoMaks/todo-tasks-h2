package com.mk.todotasksh2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.mk.todotasksh2.aop.LogExecutionTime;
import com.mk.todotasksh2.dto.UserCreateDto;
import com.mk.todotasksh2.dto.UserDto;
import com.mk.todotasksh2.dto.UserTasksDto;
import com.mk.todotasksh2.model.Role;
import com.mk.todotasksh2.service.UserService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class UsersController {

    private final UserService userService;

    @GetMapping
    @LogExecutionTime
    @Operation(summary = "Get list of users.",
            description = "Return list of all users.",
            tags = "get")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", content = @Content())
    @ApiResponse(responseCode = "403", content = @Content())
    @ApiResponse(responseCode = "500", content = @Content())
    public ResponseEntity<List<UserDto>> findAllUsers(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userService.findAllUsers(pageable));
    }

    @GetMapping("/{id}")
    @LogExecutionTime
    @Operation(summary = "Get user data by id.",
            description = "Return user data with the id passed in the path variable.",
            tags = "get")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", content = @Content())
    @ApiResponse(responseCode = "404", content = @Content())
    @ApiResponse(responseCode = "500", content = @Content())
    public ResponseEntity<UserDto> findUserById(@PathVariable("id") long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping("/{id}/tasks")
    @LogExecutionTime
    @Operation(summary = "Get user tasks.",
            description = "Return the user's task. The user ID is passed in the path variable.",
            tags = "get")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", content = @Content())
    @ApiResponse(responseCode = "404", content = @Content())
    @ApiResponse(responseCode = "500", content = @Content())
    public ResponseEntity<List<UserTasksDto>> findUserTasks(@PathVariable("id") long id) {
             return ResponseEntity.ok(userService.findUserTasks(id));
    }

    @PostMapping
    @LogExecutionTime
    @Operation(summary = "Create new user.",
            description = "Return created user.",
            tags = "post")
    @ApiResponse(responseCode = "201",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", content = @Content())
    @ApiResponse(responseCode = "404", content = @Content())
    @ApiResponse(responseCode = "500", content = @Content())
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        return new ResponseEntity<>(userService.createUser(userCreateDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @LogExecutionTime
    @Operation(summary = "Change user role(for admin only).",
            description = "Returns the user with the new role passed in the request body. " +
                    "The user ID is passed in the path variable.",
            tags = "path")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", content = @Content())
    @ApiResponse(responseCode = "403", content = @Content())
    @ApiResponse(responseCode = "404", content = @Content())
    @ApiResponse(responseCode = "500", content = @Content())
    public ResponseEntity<UserDto> changeUserRole(@PathVariable Long id, @RequestBody Role role) {
        return ResponseEntity.ok(userService.changeUserRole(id, role));
    }

    @DeleteMapping("/{id}")
    @LogExecutionTime
    @Operation(summary = "Delete user by id(for admin only).",
            description = "Deletes user with the id passed in the path variable",
            tags = "delete")
    @ApiResponse(responseCode = "204", description = "Person successfully deleted")
    @ApiResponse(responseCode = "400", description = "Invalid input Id", content = @Content)
    @ApiResponse(responseCode = "403", content = @Content())
    @ApiResponse(responseCode = "404", content = @Content)
    @ApiResponse(responseCode = "500", content = @Content)
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
