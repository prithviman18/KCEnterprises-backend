package com.KC.Enterprises.dto;

import java.time.LocalDate;
import java.util.List;

import com.KC.Enterprises.enums.PurchaseOrderStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderRequest {
    
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;
    
    @NotNull(message = "Order date is required")
    private LocalDate orderDate;
    
    @FutureOrPresent(message = "Expected delivery date must be today or in the future")
    private LocalDate expectedDeliveryDate;
    
    private PurchaseOrderStatus status;
    
    private String remarks;
    
    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<PurchaseOrderItemRequest> items;
}