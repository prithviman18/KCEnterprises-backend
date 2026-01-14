package com.KC.Enterprises.service.impl;

import com.KC.Enterprises.dto.*;
import com.KC.Enterprises.entity.*;
import com.KC.Enterprises.enums.PaymentMethod;
import com.KC.Enterprises.exception.BusinessException;
import com.KC.Enterprises.exception.ResourceNotFoundException;
import com.KC.Enterprises.repository.*;
import com.KC.Enterprises.service.SchoolPaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolPaymentServiceImpl implements SchoolPaymentService {

    // Dependencies - these are automatically injected by Spring
    private final SchoolPaymentRepository schoolPaymentRepository;
    private final SchoolRepository schoolRepository;
    private final SchoolPurchaseOrderRepository purchaseOrderRepository;
    
    @Override
    @Transactional
    public SchoolPaymentResponse createPayment(SchoolPaymentRequest request) {
        log.info("Creating school payment for school ID: {}", request.getSchoolId());
        
        // Step 1: Validate the school exists
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School not found with id: " + request.getSchoolId()));
        
        // Step 2: Validate the purchase order exists
        SchoolPurchaseOrder purchaseOrder = purchaseOrderRepository.findById(request.getPurchaseOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase order not found with id: " + request.getPurchaseOrderId()));
        
        // Step 3: Validate the payment doesn't exceed order amount
        Double totalPaidSoFar = schoolPaymentRepository.getTotalAmountPaidForOrder(request.getPurchaseOrderId());
        totalPaidSoFar = totalPaidSoFar != null ? totalPaidSoFar : 0.0;
        Double totalOrderAmount = purchaseOrder.getTotalAmount();
        
        if (totalPaidSoFar + request.getAmountPaid() > totalOrderAmount) {
            throw new BusinessException(String.format(
                    "Payment amount (%.2f) exceeds remaining balance (%.2f). " +
                    "Total order amount: %.2f, Already paid: %.2f",
                    request.getAmountPaid(),
                    totalOrderAmount - totalPaidSoFar,
                    totalOrderAmount,
                    totalPaidSoFar
            ));
        }
        
        // Step 4: Convert payment method string to enum
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid payment method: " + request.getPaymentMethod());
        }
        
        // Step 5: Create the payment entity
        SchoolPayment payment = new SchoolPayment();
        payment.setSchool(school);
        payment.setPurchaseOrder(purchaseOrder);
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentDate(request.getPaymentDate() != null ? 
                request.getPaymentDate() : LocalDateTime.now());
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionReference(request.getTransactionReference());
        payment.setRemarks(request.getRemarks());
        
        // Step 6: Save to database
        SchoolPayment savedPayment = schoolPaymentRepository.save(payment);
        log.info("School payment created with ID: {}", savedPayment.getId());
        
        // Step 7: Return response
        return mapToResponse(savedPayment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SchoolPaymentResponse getPaymentById(Long id) {
        log.info("Fetching school payment with ID: {}", id);
        
        SchoolPayment payment = schoolPaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School payment not found with id: " + id));
        
        return mapToResponse(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SchoolPaymentResponse> getAllPayments() {
        log.info("Fetching all school payments");
        
        return schoolPaymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SchoolPaymentResponse> getPaymentsBySchool(Long schoolId) {
        log.info("Fetching payments for school ID: {}", schoolId);
        
        // Check if school exists
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School not found with id: " + schoolId);
        }
        
        return schoolPaymentRepository.findBySchoolId(schoolId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SchoolPaymentResponse> getPaymentsByPurchaseOrder(Long purchaseOrderId) {
        log.info("Fetching payments for purchase order ID: {}", purchaseOrderId);
        
        return schoolPaymentRepository.findByPurchaseOrderId(purchaseOrderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SchoolPaymentResponse> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching payments between {} and {}", startDate, endDate);
        
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date cannot be after end date");
        }
        
        return schoolPaymentRepository.findByPaymentDateBetween(startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SchoolPaymentResponse> getPaymentsByMethod(String paymentMethod) {
        log.info("Fetching payments by method: {}", paymentMethod);
        
        try {
            PaymentMethod method = PaymentMethod.valueOf(paymentMethod.toUpperCase());
            return schoolPaymentRepository.findByPaymentMethod(method.toString())
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid payment method: " + paymentMethod);
        }
    }
    
    @Override
    @Transactional
    public SchoolPaymentResponse updatePayment(Long id, SchoolPaymentRequest request) {
        log.info("Updating school payment with ID: {}", id);
        
        // Find existing payment
        SchoolPayment payment = schoolPaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School payment not found with id: " + id));
        
        // Validate school if changed
        if (request.getSchoolId() != null && 
            !request.getSchoolId().equals(payment.getSchool().getId())) {
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "School not found with id: " + request.getSchoolId()));
            payment.setSchool(school);
        }
        
        // Validate purchase order if changed
        if (request.getPurchaseOrderId() != null && 
            !request.getPurchaseOrderId().equals(payment.getPurchaseOrder().getId())) {
            SchoolPurchaseOrder purchaseOrder = purchaseOrderRepository.findById(request.getPurchaseOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Purchase order not found with id: " + request.getPurchaseOrderId()));
            payment.setPurchaseOrder(purchaseOrder);
        }
        
        // Update payment details
        if (request.getAmountPaid() != null) {
            payment.setAmountPaid(request.getAmountPaid());
        }
        
        if (request.getPaymentDate() != null) {
            payment.setPaymentDate(request.getPaymentDate());
        }
        
        if (request.getPaymentMethod() != null) {
            try {
                PaymentMethod paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
                payment.setPaymentMethod(paymentMethod);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid payment method: " + request.getPaymentMethod());
            }
        }
        
        if (request.getTransactionReference() != null) {
            payment.setTransactionReference(request.getTransactionReference());
        }
        
        if (request.getRemarks() != null) {
            payment.setRemarks(request.getRemarks());
        }
        
        SchoolPayment updatedPayment = schoolPaymentRepository.save(payment);
        log.info("School payment updated with ID: {}", id);
        
        return mapToResponse(updatedPayment);
    }
    
    @Override
    @Transactional
    public DeleteResponse deletePayment(Long id) {
        log.info("Deleting school payment with ID: {}", id);
        
        if (!schoolPaymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("School payment not found with id: " + id);
        }
        
        schoolPaymentRepository.deleteById(id);
        log.info("School payment deleted with ID: {}", id);
        
        return DeleteResponse.builder()
                .success(true)
                .message("School payment deleted successfully")
                .deletedId(id)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentSummaryResponse getPaymentSummary(Long schoolId) {
        log.info("Getting payment summary for school ID: {}", schoolId);
        
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School not found with id: " + schoolId));
        
        // Get all payments for this school
        List<SchoolPayment> payments = schoolPaymentRepository.findBySchoolId(schoolId);
        
        // Calculate statistics
        double totalReceived = payments.stream()
                .mapToDouble(SchoolPayment::getAmountPaid)
                .sum();
        
        long totalTransactions = payments.size();
        
        // Group by payment method
        var paymentsByMethod = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getPaymentMethod().toString(),
                        Collectors.summingDouble(SchoolPayment::getAmountPaid)
                ));
        
        return PaymentSummaryResponse.builder()
                .entityId(schoolId)
                .entityName(school.getSchoolName())
                .totalReceived(totalReceived)
                .totalTransactions(totalTransactions)
                .paymentsByMethod(paymentsByMethod)
                .lastPaymentDate(payments.isEmpty() ? null : 
                        payments.stream()
                                .map(SchoolPayment::getPaymentDate)
                                .max(LocalDateTime::compareTo)
                                .orElse(null))
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getRemainingBalance(Long purchaseOrderId) {
        log.info("Calculating remaining balance for purchase order ID: {}", purchaseOrderId);
        
        SchoolPurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase order not found with id: " + purchaseOrderId));
        
        Double totalPaid = schoolPaymentRepository.getTotalAmountPaidForOrder(purchaseOrderId);
        totalPaid = totalPaid != null ? totalPaid : 0.0;
        
        return purchaseOrder.getTotalAmount() - totalPaid;
    }
    
    /**
     * Helper method to convert Entity to Response DTO
     */
    private SchoolPaymentResponse mapToResponse(SchoolPayment payment) {
        // Calculate remaining balance for this order
        Double totalPaid = schoolPaymentRepository.getTotalAmountPaidForOrder(
                payment.getPurchaseOrder().getId());
        totalPaid = totalPaid != null ? totalPaid : 0.0;
        Double remainingBalance = payment.getPurchaseOrder().getTotalAmount() - totalPaid;
        
        // Determine payment status
        String paymentStatus;
        if (totalPaid == 0) {
            paymentStatus = "UNPAID";
        } else if (totalPaid < payment.getPurchaseOrder().getTotalAmount()) {
            paymentStatus = "PARTIAL";
        } else if (totalPaid > payment.getPurchaseOrder().getTotalAmount()) {
            paymentStatus = "OVERPAID";
        } else {
            paymentStatus = "FULL";
        }
        
        return SchoolPaymentResponse.builder()
                .id(payment.getId())
                .schoolId(payment.getSchool().getId())
                .schoolName(payment.getSchool().getSchoolName())
                .purchaseOrderId(payment.getPurchaseOrder().getId())
                .amountPaid(payment.getAmountPaid())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod().toString())
                .transactionReference(payment.getTransactionReference())
                .remarks(payment.getRemarks())
                .createdAt(payment.getCreatedAt())
                .orderTotalAmount(payment.getPurchaseOrder().getTotalAmount())
                .remainingBalance(remainingBalance)
                .paymentStatus(paymentStatus)
                .build();
    }
}