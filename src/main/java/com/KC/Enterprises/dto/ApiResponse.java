package com.KC.Enterprises.dto;

import java.time.LocalDateTime;
import java.util.List;


import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;


import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private ErrorResponse error;
    private Meta meta; // This will use your custom Meta class

    // Private constructor
    private ApiResponse() {}

    // Success methods
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.message = "Operation completed successfully";
        response.meta = new Meta(LocalDateTime.now());
        return response;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.message = message;
        response.meta = new Meta(LocalDateTime.now());
        return response;
    }

    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = message;
        response.meta = new Meta(LocalDateTime.now());
        return response;
    }

    // Error methods
    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.error = new ErrorResponse(status.value(), status.getReasonPhrase(), LocalDateTime.now());
        response.meta = new Meta(LocalDateTime.now());
        return response;
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus status, List<ErrorResponse.ValidationError> details) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.error = new ErrorResponse(status.value(), status.getReasonPhrase(), LocalDateTime.now(), details);
        response.meta = new Meta(LocalDateTime.now());
        return response;
    }
}