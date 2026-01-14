package com.KC.Enterprises.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.KC.Enterprises.enums.PurchaseOrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderSummaryResponse {
    
    private Long id;
    private Long supplierId;
    private String supplierName;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private PurchaseOrderStatus status;
    private Double totalAmount;
    private Integer totalItems;
    private LocalDateTime createdAt;
}