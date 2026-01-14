package com.KC.Enterprises.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SupplierEmployeeRequest {
    @NotBlank(message = "Employee name is required")
    private String employeeName;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String designation;

    private String email;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;
}
