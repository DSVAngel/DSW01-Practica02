package com.dsw.practica02.config;

import com.dsw.practica02.departamentos.exception.DepartamentoBadRequestException;
import com.dsw.practica02.departamentos.exception.DepartamentoConflictException;
import com.dsw.practica02.departamentos.exception.DepartamentoDuplicateKeyException;
import com.dsw.practica02.departamentos.exception.DepartamentoNotFoundException;
import com.dsw.practica02.empleados.exception.BadRequestException;
import com.dsw.practica02.empleados.exception.DuplicateKeyException;
import com.dsw.practica02.empleados.exception.NotFoundException;
import com.dsw.practica02.empleados.exception.PreconditionFailedException;
import com.dsw.practica02.empleados.exception.PreconditionRequiredException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiError.of("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiError> handleConflict(DuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiError.of("DUPLICATE_KEY", ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiError.of("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(PreconditionFailedException.class)
    public ResponseEntity<ApiError> handlePreconditionFailed(PreconditionFailedException ex) {
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
            .body(ApiError.of("PRECONDITION_FAILED", ex.getMessage()));
    }

    @ExceptionHandler(PreconditionRequiredException.class)
    public ResponseEntity<ApiError> handlePreconditionRequired(PreconditionRequiredException ex) {
        return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED)
            .body(ApiError.of("PRECONDITION_REQUIRED", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::formatFieldError)
            .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiError.of("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiError.of("VALIDATION_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(DepartamentoNotFoundException.class)
    public ResponseEntity<ApiError> handleDepartamentoNotFound(DepartamentoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiError.of("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(DepartamentoDuplicateKeyException.class)
    public ResponseEntity<ApiError> handleDepartamentoDuplicateKey(DepartamentoDuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiError.of("DUPLICATE_KEY", ex.getMessage()));
    }

    @ExceptionHandler(DepartamentoConflictException.class)
    public ResponseEntity<ApiError> handleDepartamentoConflict(DepartamentoConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiError.of("CONFLICT", ex.getMessage()));
    }

    @ExceptionHandler(DepartamentoBadRequestException.class)
    public ResponseEntity<ApiError> handleDepartamentoBadRequest(DepartamentoBadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiError.of("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiError.of("INTERNAL_ERROR", "Unexpected error"));
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
