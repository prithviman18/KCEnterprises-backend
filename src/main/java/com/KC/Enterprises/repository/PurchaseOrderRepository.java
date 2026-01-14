package com.KC.Enterprises.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.KC.Enterprises.entity.PurchaseOrder;
import com.KC.Enterprises.enums.PurchaseOrderStatus;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    
    List<PurchaseOrder> findBySupplierId(Long supplierId);
    
    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);
    
    List<PurchaseOrder> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.supplier.id = :supplierId AND po.status = :status")
    List<PurchaseOrder> findBySupplierIdAndStatus(@Param("supplierId") Long supplierId, 
                                                  @Param("status") PurchaseOrderStatus status);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.expectedDeliveryDate < :date AND po.status NOT IN ('DELIVERED', 'CANCELLED')")
    List<PurchaseOrder> findOverdueOrders(@Param("date") LocalDate date);
    
    @Query("SELECT SUM(po.totalAmount) FROM PurchaseOrder po WHERE po.supplier.id = :supplierId")
    Double getTotalPurchaseAmountBySupplier(@Param("supplierId") Long supplierId);
    
    @Query("SELECT DISTINCT po FROM PurchaseOrder po LEFT JOIN FETCH po.items WHERE po.id = :id")
    Optional<PurchaseOrder> findByIdWithItems(@Param("id") Long id);
}