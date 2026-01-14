package com.KC.Enterprises.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailsRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Class level is required")
    private String classLevel;
    
    private String subject;
    
    private String board;
    private String edition;
    private String isbn;
    private String publisher;
}