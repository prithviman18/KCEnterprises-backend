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
public class SchoolResponse {
    
    private Long id;
    private String schoolName;
    private String address;
    private String contactPersonName;
    private String contactPhone;
    private String contactEmail;
    private String gstNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}