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
public class SchoolPaymentResponse {
    
    /**
     * Response DTO - what we send back to the user
     * Contains all payment details plus related info
     */
    
    private Long id;
    private Long schoolId;
    private String schoolName;
    private Long purchaseOrderId;
    private String orderNumber; // You might want to add this to your entity
    private Double amountPaid;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String transactionReference;
    private String remarks;
    private LocalDateTime createdAt;
    
    // Additional useful information
    private Double orderTotalAmount; // Total amount of the purchase order
    private Double remainingBalance; // How much is still due
    private String paymentStatus; // FULL, PARTIAL, OVERPAID
}