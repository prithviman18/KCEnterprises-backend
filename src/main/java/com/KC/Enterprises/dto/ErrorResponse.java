package com.KC.Enterprises.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int code;
    private String status;
    private LocalDateTime timestamp;
    private List<ValidationError> details;
    private String path;
    private String type;
    private String title;
    private String instance;

    // Constructor for basic error
    public ErrorResponse(int code, String status, LocalDateTime timestamp) {
        this.code = code;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Constructor with details
    public ErrorResponse(int code, String status, LocalDateTime timestamp, List<ValidationError> details) {
        this.code = code;
        this.status = status;
        this.timestamp = timestamp;
        this.details = details;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}