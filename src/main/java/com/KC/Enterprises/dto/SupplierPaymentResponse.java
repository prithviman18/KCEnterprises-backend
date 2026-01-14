package com.KC.Enterprises.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPaymentResponse {
    
    private Long id;
    private Long supplierId;
    private String supplierName;
    private Long purchaseOrderId;
    private String orderNumber;
    private Double amountPaid;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String transactionReference;
    private String remarks;
    private LocalDateTime createdAt;
    
    // Additional info
    private Double orderTotalAmount;
    private Double remainingBalance;
    private Boolean isOverdue;
}