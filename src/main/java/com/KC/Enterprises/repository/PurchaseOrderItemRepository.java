package com.KC.Enterprises.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.KC.Enterprises.entity.PurchaseOrderItem;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem,Long> {
    List<PurchaseOrderItem> findByPurchaseOrderId(Long purchaseOrderId);

    @Modifying
    @Query("DELETE FROM PurchaseOrderItem i WHERE i.purchaseOrder.id = :orderId")
    void deleteByPurchaseOrderId(@Param("orderId") Long orderId);
}
