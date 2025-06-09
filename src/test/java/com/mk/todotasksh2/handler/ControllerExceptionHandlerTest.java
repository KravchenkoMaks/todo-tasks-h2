package com.mk.todotasksh2.handler;

import com.mk.todotasksh2.error.ApiError;
import com.mk.todotasksh2.exeption.NotChangeStatusException;
import com.mk.todotasksh2.model.TaskState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerExceptionHandlerTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    WebRequest webRequest;

    private ControllerExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ControllerExceptionHandler(messageSource);
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/test");
    }

    @Test
    void handleInvalidInputDtoException_ShouldReturnBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getFieldErrors()).thenReturn(List.of());

        var response = exceptionHandler.handleInvalidInputDtoException(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(response.getBody()).error()).isEqualTo("Bad Request");
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    void handleInvalidInputParameterException_ShouldReturnBadRequest() {
        HandlerMethodValidationException ex = mock(HandlerMethodValidationException.class);
        when(ex.getAllValidationResults()).thenReturn(List.of());

        ResponseEntity<ApiError> response = exceptionHandler.handleInvalidInputParameterException(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(response.getBody()).error()).isEqualTo("Bad Request");
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    void handleResponseStatusException_ShouldReturnCustomStatus() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found");
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Not Found");

        ResponseEntity<ApiError> response = exceptionHandler.handleResponseStatusException(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(Objects.requireNonNull(response.getBody()).error()).isEqualTo("Not Found");
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    void globalExceptionHandler_ShouldReturnInternalServerError() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<ApiError> response = exceptionHandler.globalExceptionHandler(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(Objects.requireNonNull(response.getBody()).error()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().status()).isEqualTo(500);
    }

    @Test
    void globalExceptionHandler_ShouldReturnBadRequestForTypeMismatch() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getMessage()).thenReturn("massage");

        ResponseEntity<ApiError> response = exceptionHandler.globalExceptionHandler(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(response.getBody()).error()).isEqualTo("Bad Request");
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    void globalExceptionHandler_ShouldReturnBadRequestForBindException() {
        BindException ex = mock(BindException.class);
        when(ex.getMessage()).thenReturn("massage");

        ResponseEntity<ApiError> response = exceptionHandler.globalExceptionHandler(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(response.getBody()).error()).isEqualTo("Bad Request");
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    void testHandlerNotChangeStatusException() {
        NotChangeStatusException exception = mock(NotChangeStatusException.class);

        when(webRequest.getDescription(false)).thenReturn("uri=/api/tasks");
        when(exception.getBundle()).thenReturn("Status transition not allowed");
        when(exception.getAvailableStates()).thenReturn(EnumSet.of(TaskState.DONE));

        ResponseEntity<ApiError> responseEntity = exceptionHandler.handlerNotChangeStatusException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

}