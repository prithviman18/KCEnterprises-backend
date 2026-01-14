package com.KC.Enterprises.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryResponse {
    
    /**
     * Generic summary DTO for payment statistics
     * Can be used for both school and supplier payments
     */
    
    private Long entityId;               // School ID or Supplier ID
    private String entityName;           // School name or Supplier name
    
    // For school payments (money received FROM schools)
    private Double totalReceived;
    
    // For supplier payments (money paid TO suppliers)
    private Double totalPaid;
    
    private Long totalTransactions;      // Total number of payment transactions
    private LocalDateTime lastPaymentDate; // Date of most recent payment
    
    // Breakdown by payment method (CASH: 5000.00, BANK_TRANSFER: 15000.00, etc.)
    private Map<String, Double> paymentsByMethod;
    
    // Additional useful statistics
    private Double averagePaymentAmount; // Average amount per payment
    private Double largestPaymentAmount; // Largest single payment
    private Double smallestPaymentAmount; // Smallest single payment
    
    // Monthly/Yearly trends (optional, can be added later)
    // private Map<String, Double> monthlyTrends;
}