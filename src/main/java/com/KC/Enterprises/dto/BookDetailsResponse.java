package com.KC.Enterprises.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailsResponse {
    
    private Long id;
    private String title;
    private String classLevel;
    private String subject;
    private String board;
    private String edition;
    private String isbn;
    private String publisher;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}