package com.vuog.telebotmanager.infrastructure.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BotNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBotNotFoundException(BotNotFoundException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            errors.toString(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage(), request);
    }
    
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, status);
    }
    
    public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
    ) {}
}