package com.KC.Enterprises.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.KC.Enterprises.entity.SchoolPurchaseOrderItem;

public interface  SchoolPurchaseOrderItemRepository extends JpaRepository<SchoolPurchaseOrderItem,Long> {
    List<SchoolPurchaseOrderItem> findByPurchaseOrderId(Long purchaseOrderId);
    
    @Modifying
    @Query("DELETE FROM SchoolPurchaseOrderItem i WHERE i.purchaseOrder.id = :orderId")
    void deleteByPurchaseOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT i FROM SchoolPurchaseOrderItem i WHERE i.purchaseOrder.school.id = :schoolId AND i.product.id = :productId")
    List<SchoolPurchaseOrderItem> findBySchoolAndProduct(@Param("schoolId") Long schoolId, 
                                                        @Param("productId") Long productId);
    
    @Query("SELECT SUM(i.totalAmount) FROM SchoolPurchaseOrderItem i WHERE i.purchaseOrder.school.id = :schoolId")
    Double getTotalAmountBySchool(@Param("schoolId") Long schoolId);
    
    @Query("SELECT i.product.id, SUM(i.quantity) as totalQuantity " +
           "FROM SchoolPurchaseOrderItem i " +
           "WHERE i.purchaseOrder.school.id = :schoolId " +
           "GROUP BY i.product.id")
    List<Object[]> getProductQuantitiesBySchool(@Param("schoolId") Long schoolId);
    
}
