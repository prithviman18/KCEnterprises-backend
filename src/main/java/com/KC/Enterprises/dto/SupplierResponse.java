package com.KC.Enterprises.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierResponse {
    private Long id;
    private String supplierName;
    private String email;
    private String mobile;
    private String address;
    private String contactPerson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
