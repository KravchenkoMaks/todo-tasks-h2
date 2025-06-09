package com.mk.todotasksh2.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.mk.todotasksh2.error.ApiError;
import com.mk.todotasksh2.exeption.NotChangeStatusException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    protected ResponseEntity<ApiError> handleInvalidInputDtoException(MethodArgumentNotValidException ex,
                                                                      WebRequest request) {
        var errors = ex.getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();

        log.error("Validation failed: {}", errors, ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createApiError(request, HttpStatus.BAD_REQUEST, errors));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleInvalidInputParameterException(
            HandlerMethodValidationException ex, WebRequest request) {

        var errors = ex.getAllValidationResults().stream()
                .flatMap(validationResult -> validationResult.getResolvableErrors().stream())
                .map(messageSourceResolvable -> {
                    String argumentMessages = "";
                    if (messageSourceResolvable.getArguments() != null) {
                        argumentMessages = Arrays.stream(messageSourceResolvable.getArguments())
                                .filter(MessageSourceResolvable.class::isInstance)
                                .map(arg -> ((MessageSourceResolvable) arg).getDefaultMessage())
                                .collect(Collectors.joining(", "));
                    }
                    return "Parameter " + argumentMessages + ": " + messageSourceResolvable.getDefaultMessage();
                })
                .toList();

        log.error("Parameter validation failed: {}", errors, ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createApiError(request, HttpStatus.BAD_REQUEST, errors));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                ex.getBody().getStatus(),
                ex.getBody().getTitle(),
                List.of(createMessage(ex.getReason())),
                URI.create(request.getDescription(false)).getPath().substring(4));

        log.error("ResponseStatusException occurred: {}", apiError, ex);

        return new ResponseEntity<>(apiError, ex.getStatusCode());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> handlerAuthorizationDeniedException(AuthorizationDeniedException ex, WebRequest request) {
        log.error("AuthorizationDeniedException occurred", ex);

        HttpStatus status = HttpStatus.FORBIDDEN;
        var message = createMessage("error.AccessDenied.message");

        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
            status = HttpStatus.UNAUTHORIZED;
            message = createMessage("error.NotAuthentication.message");
        }

        return ResponseEntity.status(status)
                .body(createApiError(request, status, List.of(message)));
    }

    @ExceptionHandler(NotChangeStatusException.class)
    public ResponseEntity<ApiError> handlerNotChangeStatusException(NotChangeStatusException ex, WebRequest request) {
        log.error("NotChangeStatusException occurred", ex);
        String message = createMessage(ex.getBundle());
        String availableStates = ex.getAvailableStates().stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
        var errorMessage = message + ": " + availableStates + ".";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createApiError(request, HttpStatus.BAD_REQUEST, List.of(errorMessage)));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> globalExceptionHandler(Exception ex, WebRequest request) {
        log.error("An unexpected error occurred", ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof MethodArgumentTypeMismatchException
                || ex instanceof BindException
                || ex instanceof HttpMessageNotReadableException) {
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status)
                .body(createApiError(request, status, List.of(ex.getMessage())));
    }

    private ApiError createApiError(WebRequest request, HttpStatus status, List<String> errors) {
        return new ApiError(LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                errors,
                URI.create(request.getDescription(false)).getPath().substring(4));
    }

    private String createMessage(String reason) {
        return messageSource.getMessage(Objects.requireNonNull(reason), null, LocaleContextHolder.getLocale());
    }


}
