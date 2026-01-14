package com.KC.Enterprises.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.KC.Enterprises.entity.SchoolPurchaseOrder;
import com.KC.Enterprises.enums.SchoolOrderStatus;

@Repository
public interface SchoolPurchaseOrderRepository extends JpaRepository<SchoolPurchaseOrder,Long>{
    List<SchoolPurchaseOrder> findBySchoolId(Long schoolId);
    
    List<SchoolPurchaseOrder> findByStatus(SchoolOrderStatus status);
    
    List<SchoolPurchaseOrder> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT spo FROM SchoolPurchaseOrder spo WHERE spo.school.id = :schoolId AND spo.status = :status")
    List<SchoolPurchaseOrder> findBySchoolIdAndStatus(@Param("schoolId") Long schoolId, 
                                                      @Param("status") SchoolOrderStatus status);
    
    @Query("SELECT spo FROM SchoolPurchaseOrder spo WHERE spo.deliveryDate < :date AND spo.status NOT IN ('DELIVERED', 'CANCELLED')")
    List<SchoolPurchaseOrder> findOverdueOrders(@Param("date") LocalDateTime date);
    
    @Query("SELECT SUM(spo.totalAmount) FROM SchoolPurchaseOrder spo WHERE spo.school.id = :schoolId")
    Double getTotalPurchaseAmountBySchool(@Param("schoolId") Long schoolId);
    
    @Query("SELECT DISTINCT spo FROM SchoolPurchaseOrder spo LEFT JOIN FETCH spo.items WHERE spo.id = :id")
    Optional<SchoolPurchaseOrder> findByIdWithItems(@Param("id") Long id);
    
    @Query("SELECT spo FROM SchoolPurchaseOrder spo " +
           "LEFT JOIN FETCH spo.items " +
           "LEFT JOIN FETCH spo.school " +
           "WHERE spo.id = :id")
    Optional<SchoolPurchaseOrder> findByIdWithItemsAndSchool(@Param("id") Long id);
    
    boolean existsBySchoolIdAndStatus(Long schoolId, SchoolOrderStatus status);
}
