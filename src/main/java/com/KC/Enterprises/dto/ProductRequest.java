package com.KC.Enterprises.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Category is required")
    private String category;

    private String brand;

    private String unitOfMeasure;

    @NotNull(message = "Cost price is required")
    @Positive(message = "Cost price must be positive")
    private Double costPrice;

    @NotNull(message = "MRP is required")
    @Positive(message = "MRP must be positive")
    private Double mrp;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    private Long bookDetailsId; // Optional
}