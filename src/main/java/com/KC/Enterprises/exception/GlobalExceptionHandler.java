package com.KC.Enterprises.exception;

import com.KC.Enterprises.dto.ApiResponse;
import com.KC.Enterprises.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Helper method to check if request is for Swagger/OpenAPI
    private boolean isSwaggerRequest(WebRequest request) {
        String description = request.getDescription(false);
        return description.contains("/v3/api-docs") || 
               description.contains("/swagger-ui") ||
               description.contains("/swagger-resources");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        if (isSwaggerRequest(request)) {
            return null; // Skip handling for Swagger requests
        }
        
        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    Object rejectedValue = ((FieldError) error).getRejectedValue();
                    
                    return new ErrorResponse.ValidationError(fieldName, errorMessage, rejectedValue);
                })
                .collect(Collectors.toList());

        ApiResponse<Object> response = ApiResponse.error(
                "Validation failed",
                HttpStatus.BAD_REQUEST,
                validationErrors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        
        if (isSwaggerRequest(request)) {
            return null; // Skip handling for Swagger requests
        }
        
        ApiResponse<Object> response = ApiResponse.error(
                "Invalid email or password",
                HttpStatus.UNAUTHORIZED
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        if (isSwaggerRequest(request)) {
            return null; // Skip handling for Swagger requests
        }
        
        ApiResponse<Object> response = ApiResponse.error(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        if (isSwaggerRequest(request)) {
            return null; // Skip handling for Swagger requests
        }
        
        ApiResponse<Object> response = ApiResponse.error(
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}