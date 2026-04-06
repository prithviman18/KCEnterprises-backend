package com.KC.Enterprises.controller;

import com.KC.Enterprises.dto.*;
import com.KC.Enterprises.service.SupplierPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/supplier-payments")
@RequiredArgsConstructor
@Tag(name = "Supplier Payments", description = "APIs for managing payments to suppliers")
@SecurityRequirement(name = "Bearer Authentication")
public class SupplierPaymentController {

    private final SupplierPaymentService supplierPaymentService;

    @PostMapping
    @Operation(summary = "Create a new supplier payment")
    public ResponseEntity<ApiResponse<SupplierPaymentResponse>> createPayment(
            @Valid @RequestBody SupplierPaymentRequest request) {
        SupplierPaymentResponse response = supplierPaymentService.createPayment(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Supplier payment recorded successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<ApiResponse<SupplierPaymentResponse>> getPaymentById(@PathVariable Long id) {
        SupplierPaymentResponse response = supplierPaymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all supplier payments")
    public ResponseEntity<ApiResponse<List<SupplierPaymentResponse>>> getAllPayments() {
        List<SupplierPaymentResponse> payments = supplierPaymentService.getAllPayments();
        return ResponseEntity.ok(ApiResponse.success(payments, "Payments retrieved successfully"));
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Get payments by supplier")
    public ResponseEntity<ApiResponse<List<SupplierPaymentResponse>>> getPaymentsBySupplier(
            @PathVariable Long supplierId) {
        List<SupplierPaymentResponse> payments = supplierPaymentService.getPaymentsBySupplier(supplierId);
        return ResponseEntity.ok(ApiResponse.success(payments, "Supplier payments retrieved successfully"));
    }

    @GetMapping("/order/{purchaseOrderId}")
    @Operation(summary = "Get payments by purchase order")
    public ResponseEntity<ApiResponse<List<SupplierPaymentResponse>>> getPaymentsByPurchaseOrder(
            @PathVariable Long purchaseOrderId) {
        List<SupplierPaymentResponse> payments = supplierPaymentService.getPaymentsByPurchaseOrder(purchaseOrderId);
        return ResponseEntity.ok(ApiResponse.success(payments, "Order payments retrieved successfully"));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get payments by date range")
    public ResponseEntity<ApiResponse<List<SupplierPaymentResponse>>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<SupplierPaymentResponse> payments = supplierPaymentService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(payments, "Payments retrieved successfully"));
    }

    @GetMapping("/method/{paymentMethod}")
    @Operation(summary = "Get payments by payment method")
    public ResponseEntity<ApiResponse<List<SupplierPaymentResponse>>> getPaymentsByMethod(
            @PathVariable String paymentMethod) {
        List<SupplierPaymentResponse> payments = supplierPaymentService.getPaymentsByMethod(paymentMethod);
        return ResponseEntity.ok(ApiResponse.success(payments, "Payments retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment")
    public ResponseEntity<ApiResponse<SupplierPaymentResponse>> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody SupplierPaymentRequest request) {
        SupplierPaymentResponse response = supplierPaymentService.updatePayment(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment")
    public ResponseEntity<ApiResponse<DeleteResponse>> deletePayment(@PathVariable Long id) {
        DeleteResponse response = supplierPaymentService.deletePayment(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment deleted successfully"));
    }

    @GetMapping("/summary/supplier/{supplierId}")
    @Operation(summary = "Get payment summary for a supplier")
    public ResponseEntity<ApiResponse<PaymentSummaryResponse>> getPaymentSummary(
            @PathVariable Long supplierId) {
        PaymentSummaryResponse response = supplierPaymentService.getSupplierPaymentSummary(supplierId);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment summary retrieved successfully"));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue payments")
    public ResponseEntity<ApiResponse<List<SupplierPaymentResponse>>> getOverduePayments() {
        List<SupplierPaymentResponse> payments = supplierPaymentService.getOverduePayments();
        return ResponseEntity.ok(ApiResponse.success(payments, "Overdue payments retrieved successfully"));
    }

    @GetMapping("/outstanding/supplier/{supplierId}")
    @Operation(summary = "Get total outstanding to supplier")
    public ResponseEntity<ApiResponse<Double>> getTotalOutstanding(
            @PathVariable Long supplierId) {
        Double outstanding = supplierPaymentService.getTotalOutstandingToSupplier(supplierId);
        return ResponseEntity.ok(ApiResponse.success(outstanding, "Outstanding amount retrieved successfully"));
    }
}