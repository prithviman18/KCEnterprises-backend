package com.KC.Enterprises.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierEmployeeResponse {
    private Long id;
    private String employeeName;
    private String phone;
    private String designation;
    private String email;
    private Long supplierId;
    private String supplierName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
