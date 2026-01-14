package com.KC.Enterprises.service;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.PaymentSummaryResponse;
import com.KC.Enterprises.dto.SchoolPaymentRequest;
import com.KC.Enterprises.dto.SchoolPaymentResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface SchoolPaymentService {
    
    /**
     * CREATE: Record a new payment from a school
     */
    SchoolPaymentResponse createPayment(SchoolPaymentRequest request);
    
    /**
     * READ: Get payment by ID
     */
    SchoolPaymentResponse getPaymentById(Long id);
    
    /**
     * READ: Get all payments
     */
    List<SchoolPaymentResponse> getAllPayments();
    
    /**
     * READ: Get payments by school
     */
    List<SchoolPaymentResponse> getPaymentsBySchool(Long schoolId);
    
    /**
     * READ: Get payments by purchase order
     */
    List<SchoolPaymentResponse> getPaymentsByPurchaseOrder(Long purchaseOrderId);
    
    /**
     * READ: Search payments by date range
     */
    List<SchoolPaymentResponse> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * READ: Get payments by payment method
     */
    List<SchoolPaymentResponse> getPaymentsByMethod(String paymentMethod);
    
    /**
     * UPDATE: Update payment details
     */
    SchoolPaymentResponse updatePayment(Long id, SchoolPaymentRequest request);
    
    /**
     * DELETE: Remove a payment
     */
    DeleteResponse deletePayment(Long id);
    
    /**
     * BUSINESS LOGIC: Get payment summary for a school
     */
    PaymentSummaryResponse getPaymentSummary(Long schoolId);
    
    /**
     * BUSINESS LOGIC: Calculate remaining balance for an order
     */
    Double getRemainingBalance(Long purchaseOrderId);
}