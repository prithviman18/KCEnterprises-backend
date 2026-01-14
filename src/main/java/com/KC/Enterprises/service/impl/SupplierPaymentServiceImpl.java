package com.KC.Enterprises.service.impl;

import com.KC.Enterprises.dto.*;
import com.KC.Enterprises.entity.*;
import com.KC.Enterprises.enums.PaymentMethod;
import com.KC.Enterprises.exception.BusinessException;
import com.KC.Enterprises.exception.ResourceNotFoundException;
import com.KC.Enterprises.repository.*;
import com.KC.Enterprises.service.SupplierPaymentService;

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
public class SupplierPaymentServiceImpl implements SupplierPaymentService {

    private final SupplierPaymentRepository supplierPaymentRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    
    @Override
    @Transactional
    public SupplierPaymentResponse createPayment(SupplierPaymentRequest request) {
        log.info("Creating supplier payment for supplier ID: {}", request.getSupplierId());
        
        // Validate supplier exists
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + request.getSupplierId()));
        
        // Validate purchase order exists
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(request.getPurchaseOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase order not found with id: " + request.getPurchaseOrderId()));
        
        // Convert payment method string to enum
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid payment method: " + request.getPaymentMethod());
        }
        
        // Check if payment exceeds order amount
        Double totalPaidSoFar = getTotalPaidForOrder(request.getPurchaseOrderId());
        if (totalPaidSoFar + request.getAmountPaid() > purchaseOrder.getTotalAmount()) {
            throw new BusinessException(String.format(
                    "Payment amount (%.2f) exceeds remaining payable amount (%.2f)",
                    request.getAmountPaid(),
                    purchaseOrder.getTotalAmount() - totalPaidSoFar
            ));
        }
        
        // Create payment entity
        SupplierPayment payment = new SupplierPayment();
        payment.setSupplier(supplier);
        payment.setPurchaseOrder(purchaseOrder);
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentDate(request.getPaymentDate() != null ? 
                request.getPaymentDate() : LocalDateTime.now());
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionReference(request.getTransactionReference());
        payment.setRemarks(request.getRemarks());
        
        SupplierPayment savedPayment = supplierPaymentRepository.save(payment);
        log.info("Supplier payment created with ID: {}", savedPayment.getId());
        
        return mapToResponse(savedPayment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SupplierPaymentResponse getPaymentById(Long id) {
        log.info("Fetching supplier payment with ID: {}", id);
        
        SupplierPayment payment = supplierPaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier payment not found with id: " + id));
        
        return mapToResponse(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierPaymentResponse> getAllPayments() {
        log.info("Fetching all supplier payments");
        
        return supplierPaymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierPaymentResponse> getPaymentsBySupplier(Long supplierId) {
        log.info("Fetching payments for supplier ID: {}", supplierId);
        
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + supplierId);
        }
        
        return supplierPaymentRepository.findBySupplierId(supplierId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierPaymentResponse> getPaymentsByPurchaseOrder(Long purchaseOrderId) {
        log.info("Fetching payments for purchase order ID: {}", purchaseOrderId);
        
        return supplierPaymentRepository.findByPurchaseOrderId(purchaseOrderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierPaymentResponse> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching supplier payments between {} and {}", startDate, endDate);
        
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date cannot be after end date");
        }
        
        return supplierPaymentRepository.findByPaymentDateBetween(startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierPaymentResponse> getPaymentsByMethod(String paymentMethod) {
        log.info("Fetching supplier payments by method: {}", paymentMethod);
        
        try {
            PaymentMethod method = PaymentMethod.valueOf(paymentMethod.toUpperCase());
            return supplierPaymentRepository.findByPaymentMethod(method.toString())
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid payment method: " + paymentMethod);
        }
    }
    
    @Override
    @Transactional
    public SupplierPaymentResponse updatePayment(Long id, SupplierPaymentRequest request) {
        log.info("Updating supplier payment with ID: {}", id);
        
        SupplierPayment payment = supplierPaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier payment not found with id: " + id));
        
        // Update supplier if changed
        if (request.getSupplierId() != null && 
            !request.getSupplierId().equals(payment.getSupplier().getId())) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Supplier not found with id: " + request.getSupplierId()));
            payment.setSupplier(supplier);
        }
        
        // Update purchase order if changed
        if (request.getPurchaseOrderId() != null && 
            !request.getPurchaseOrderId().equals(payment.getPurchaseOrder().getId())) {
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(request.getPurchaseOrderId())
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
        
        SupplierPayment updatedPayment = supplierPaymentRepository.save(payment);
        log.info("Supplier payment updated with ID: {}", id);
        
        return mapToResponse(updatedPayment);
    }
    
    @Override
    @Transactional
    public DeleteResponse deletePayment(Long id) {
        log.info("Deleting supplier payment with ID: {}", id);
        
        if (!supplierPaymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier payment not found with id: " + id);
        }
        
        supplierPaymentRepository.deleteById(id);
        log.info("Supplier payment deleted with ID: {}", id);
        
        return DeleteResponse.builder()
                .success(true)
                .message("Supplier payment deleted successfully")
                .deletedId(id)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentSummaryResponse getSupplierPaymentSummary(Long supplierId) {
        log.info("Getting payment summary for supplier ID: {}", supplierId);
        
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + supplierId));
        
        List<SupplierPayment> payments = supplierPaymentRepository.findBySupplierId(supplierId);
        
        double totalPaid = payments.stream()
                .mapToDouble(SupplierPayment::getAmountPaid)
                .sum();
        
        long totalTransactions = payments.size();
        
        var paymentsByMethod = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getPaymentMethod().toString(),
                        Collectors.summingDouble(SupplierPayment::getAmountPaid)
                ));
        
        return PaymentSummaryResponse.builder()
                .entityId(supplierId)
                .entityName(supplier.getSupplierName())
                .totalPaid(totalPaid)
                .totalTransactions(totalTransactions)
                .paymentsByMethod(paymentsByMethod)
                .lastPaymentDate(payments.isEmpty() ? null : 
                        payments.stream()
                                .map(SupplierPayment::getPaymentDate)
                                .max(LocalDateTime::compareTo)
                                .orElse(null))
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierPaymentResponse> getOverduePayments() {
        log.info("Fetching overdue supplier payments");
        
        return supplierPaymentRepository.findOverduePayments()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getTotalOutstandingToSupplier(Long supplierId) {
        log.info("Calculating total outstanding to supplier ID: {}", supplierId);
        
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + supplierId);
        }
        
        // Get all purchase orders for this supplier
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findBySupplierId(supplierId);
        
        double totalOutstanding = 0.0;
        
        for (PurchaseOrder order : purchaseOrders) {
            Double totalPaid = getTotalPaidForOrder(order.getId());
            totalOutstanding += order.getTotalAmount() - (totalPaid != null ? totalPaid : 0.0);
        }
        
        return totalOutstanding;
    }
    
    /**
     * Helper method to get total paid for a purchase order
     */
    private Double getTotalPaidForOrder(Long purchaseOrderId) {
        return supplierPaymentRepository.findByPurchaseOrderId(purchaseOrderId)
                .stream()
                .mapToDouble(SupplierPayment::getAmountPaid)
                .sum();
    }
    
    /**
     * Helper method to convert Entity to Response DTO
     */
    private SupplierPaymentResponse mapToResponse(SupplierPayment payment) {
        // Calculate remaining balance
        Double totalPaid = getTotalPaidForOrder(payment.getPurchaseOrder().getId());
        Double remainingBalance = payment.getPurchaseOrder().getTotalAmount() - totalPaid;
        
        // Check if payment is overdue (paid after expected delivery)
        boolean isOverdue = payment.getPaymentDate()
            .toLocalDate()
            .isAfter(payment.getPurchaseOrder().getExpectedDeliveryDate());
        
        return SupplierPaymentResponse.builder()
                .id(payment.getId())
                .supplierId(payment.getSupplier().getId())
                .supplierName(payment.getSupplier().getSupplierName())
                .purchaseOrderId(payment.getPurchaseOrder().getId())
                .amountPaid(payment.getAmountPaid())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod().toString())
                .transactionReference(payment.getTransactionReference())
                .remarks(payment.getRemarks())
                .createdAt(payment.getCreatedAt())
                .orderTotalAmount(payment.getPurchaseOrder().getTotalAmount())
                .remainingBalance(remainingBalance)
                .isOverdue(isOverdue)
                .build();
    }
}