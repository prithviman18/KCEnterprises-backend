package com.KC.Enterprises.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.KC.Enterprises.entity.SupplierPayment;

public interface SupplierPaymentRepository extends JpaRepository<SupplierPayment,Long>{
    
     /**
     * Find all payments made to a specific supplier
     * Think: "Show me all payments to XYZ Supplier"
     */
     List<SupplierPayment> findBySupplierId(Long supplierId);
    
     /**
      * Find all payments for a specific purchase order
      * Think: "Show me payments made for Purchase Order #456"
      */
     List<SupplierPayment> findByPurchaseOrderId(Long purchaseOrderId);
     
     /**
      * Find payments within a date range
      * Think: "Show me supplier payments made in January"
      */
     List<SupplierPayment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
     
     /**
      * Find payments by payment method
      * Think: "Show me all bank transfers to suppliers"
      */
     List<SupplierPayment> findByPaymentMethod(String paymentMethod);
     
     /**
      * Get total amount paid to a supplier
      * Think: "What's the total paid to XYZ Supplier?"
      */
     @Query("SELECT SUM(sp.amountPaid) FROM SupplierPayment sp WHERE sp.supplier.id = :supplierId")
     Double getTotalAmountPaidToSupplier(@Param("supplierId") Long supplierId);
     
     /**
      * Find overdue payments (payments not made by expected date)
      * Think: "Which supplier payments are late?"
      */
     @Query("SELECT sp FROM SupplierPayment sp WHERE sp.paymentDate > sp.purchaseOrder.expectedDeliveryDate")
     List<SupplierPayment> findOverduePayments();
}
