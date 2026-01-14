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
public class SchoolPaymentRequest {
    
    /**
     * Which school is making the payment?
     * Required field - can't be null
     */
    @NotNull(message = "School ID is required")
    private Long schoolId;
    
    /**
     * Which purchase order is this payment for?
     * Required field - can't be null
     */
    @NotNull(message = "Purchase Order ID is required")
    private Long purchaseOrderId;
    
    /**
     * How much money is being paid?
     * Must be positive (can't pay negative amount!)
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amountPaid;
    
    /**
     * When was the payment made?
     * If not provided, we'll use current time
     */
    private LocalDateTime paymentDate;
    
    /**
     * How was the payment made? (CASH, CARD, BANK_TRANSFER, CHEQUE)
     * Required field
     */
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    /**
     * Reference number (check number, transaction ID, etc.)
     * Optional - for tracking purposes
     */
    private String transactionReference;
    
    /**
     * Any additional notes about the payment
     * Optional
     */
    private String remarks;
}