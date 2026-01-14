package com.KC.Enterprises.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPaymentRequest {
    
    /**
     * Which supplier are we paying?
     */
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;
    
    /**
     * Which purchase order is this payment for?
     */
    @NotNull(message = "Purchase Order ID is required")
    private Long purchaseOrderId;
    
    /**
     * How much are we paying?
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amountPaid;
    
    /**
     * When was the payment made?
     */
    private LocalDateTime paymentDate;
    
    /**
     * How did we pay? (CASH, BANK_TRANSFER, CHEQUE, ONLINE)
     */
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    /**
     * Reference for tracking
     */
    private String transactionReference;
    
    /**
     * Additional notes
     */
    private String remarks;
}