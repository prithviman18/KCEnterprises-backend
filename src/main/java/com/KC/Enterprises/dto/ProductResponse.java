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
public class ProductResponse {

    private Long id;
    private String productName;
    private String category;
    private String brand;
    private String unitOfMeasure;
    private Double costPrice;
    private Double mrp;
    private Long supplierId;
    private String supplierName;
    private Long bookDetailsId;
    private String bookTitle; // If you want to include book details
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}