package com.KC.Enterprises.service;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.SupplierPaymentRequest;
import com.KC.Enterprises.dto.SupplierPaymentResponse;
import com.KC.Enterprises.dto.PaymentSummaryResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface SupplierPaymentService {
    
    SupplierPaymentResponse createPayment(SupplierPaymentRequest request);
    SupplierPaymentResponse getPaymentById(Long id);
    List<SupplierPaymentResponse> getAllPayments();
    List<SupplierPaymentResponse> getPaymentsBySupplier(Long supplierId);
    List<SupplierPaymentResponse> getPaymentsByPurchaseOrder(Long purchaseOrderId);
    List<SupplierPaymentResponse> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<SupplierPaymentResponse> getPaymentsByMethod(String paymentMethod);
    SupplierPaymentResponse updatePayment(Long id, SupplierPaymentRequest request);
    DeleteResponse deletePayment(Long id);
    PaymentSummaryResponse getSupplierPaymentSummary(Long supplierId);
    List<SupplierPaymentResponse> getOverduePayments();
    Double getTotalOutstandingToSupplier(Long supplierId);
}