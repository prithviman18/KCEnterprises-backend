package com.KC.Enterprises.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.KC.Enterprises.enums.PurchaseOrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponse {
    
    private Long id;
    private Long supplierId;
    private String supplierName;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private PurchaseOrderStatus status;
    private Double totalAmount;
    private String remarks;
    private LocalDateTime createdAt;
    private List<PurchaseOrderItemResponse> items;
}