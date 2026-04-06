package com.KC.Enterprises.controller;

import com.KC.Enterprises.dto.*;
import com.KC.Enterprises.service.SchoolPaymentService;
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
@RequestMapping("/school-payments")
@RequiredArgsConstructor
@Tag(name = "School Payments", description = "APIs for managing payments from schools")
@SecurityRequirement(name = "Bearer Authentication")
public class SchoolPaymentController {

    private final SchoolPaymentService schoolPaymentService;

    @PostMapping
    @Operation(summary = "Create a new school payment")
    public ResponseEntity<ApiResponse<SchoolPaymentResponse>> createPayment(
            @Valid @RequestBody SchoolPaymentRequest request) {
        SchoolPaymentResponse response = schoolPaymentService.createPayment(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment recorded successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<ApiResponse<SchoolPaymentResponse>> getPaymentById(@PathVariable Long id) {
        SchoolPaymentResponse response = schoolPaymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all school payments")
    public ResponseEntity<ApiResponse<List<SchoolPaymentResponse>>> getAllPayments() {
        List<SchoolPaymentResponse> payments = schoolPaymentService.getAllPayments();
        return ResponseEntity.ok(ApiResponse.success(payments, "Payments retrieved successfully"));
    }

    @GetMapping("/school/{schoolId}")
    @Operation(summary = "Get payments by school")
    public ResponseEntity<ApiResponse<List<SchoolPaymentResponse>>> getPaymentsBySchool(
            @PathVariable Long schoolId) {
        List<SchoolPaymentResponse> payments = schoolPaymentService.getPaymentsBySchool(schoolId);
        return ResponseEntity.ok(ApiResponse.success(payments, "School payments retrieved successfully"));
    }

    @GetMapping("/order/{purchaseOrderId}")
    @Operation(summary = "Get payments by purchase order")
    public ResponseEntity<ApiResponse<List<SchoolPaymentResponse>>> getPaymentsByPurchaseOrder(
            @PathVariable Long purchaseOrderId) {
        List<SchoolPaymentResponse> payments = schoolPaymentService.getPaymentsByPurchaseOrder(purchaseOrderId);
        return ResponseEntity.ok(ApiResponse.success(payments, "Order payments retrieved successfully"));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get payments by date range")
    public ResponseEntity<ApiResponse<List<SchoolPaymentResponse>>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<SchoolPaymentResponse> payments = schoolPaymentService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(payments, "Payments retrieved successfully"));
    }

    @GetMapping("/method/{paymentMethod}")
    @Operation(summary = "Get payments by payment method")
    public ResponseEntity<ApiResponse<List<SchoolPaymentResponse>>> getPaymentsByMethod(
            @PathVariable String paymentMethod) {
        List<SchoolPaymentResponse> payments = schoolPaymentService.getPaymentsByMethod(paymentMethod);
        return ResponseEntity.ok(ApiResponse.success(payments, "Payments retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment")
    public ResponseEntity<ApiResponse<SchoolPaymentResponse>> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody SchoolPaymentRequest request) {
        SchoolPaymentResponse response = schoolPaymentService.updatePayment(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment")
    public ResponseEntity<ApiResponse<DeleteResponse>> deletePayment(@PathVariable Long id) {
        DeleteResponse response = schoolPaymentService.deletePayment(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment deleted successfully"));
    }

    @GetMapping("/summary/school/{schoolId}")
    @Operation(summary = "Get payment summary for a school")
    public ResponseEntity<ApiResponse<PaymentSummaryResponse>> getPaymentSummary(
            @PathVariable Long schoolId) {
        PaymentSummaryResponse response = schoolPaymentService.getPaymentSummary(schoolId);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment summary retrieved successfully"));
    }

    @GetMapping("/balance/order/{purchaseOrderId}")
    @Operation(summary = "Get remaining balance for an order")
    public ResponseEntity<ApiResponse<Double>> getRemainingBalance(
            @PathVariable Long purchaseOrderId) {
        Double balance = schoolPaymentService.getRemainingBalance(purchaseOrderId);
        return ResponseEntity.ok(ApiResponse.success(balance, "Remaining balance retrieved successfully"));
    }
}