package com.KC.Enterprises.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.KC.Enterprises.entity.SchoolPayment;

public interface SchoolPaymentRepository extends JpaRepository<SchoolPayment, Long> {

  
     List<SchoolPayment> findBySchoolId(Long schoolId);
    
     List<SchoolPayment> findByPurchaseOrderId(Long purchaseOrderId);
     
     List<SchoolPayment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
     
     List<SchoolPayment> findByPaymentMethod(String paymentMethod);
     
     @Query("SELECT SUM(sp.amountPaid) FROM SchoolPayment sp WHERE sp.school.id = :schoolId")
     Double getTotalAmountPaidBySchool(@Param("schoolId") Long schoolId);
     
     @Query("SELECT SUM(sp.amountPaid) FROM SchoolPayment sp WHERE sp.purchaseOrder.id = :purchaseOrderId")
     Double getTotalAmountPaidForOrder(@Param("purchaseOrderId") Long purchaseOrderId);
     
     List<SchoolPayment> findByTransactionReferenceContaining(String reference);
    
}
