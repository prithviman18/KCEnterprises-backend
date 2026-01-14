package com.KC.Enterprises.service;

import java.time.LocalDateTime;
import java.util.List;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.DiscountCalculationResponse;
import com.KC.Enterprises.dto.SchoolOrderStatisticsResponse;
import com.KC.Enterprises.dto.SchoolPurchaseOrderItemRequest;
import com.KC.Enterprises.dto.SchoolPurchaseOrderRequest;
import com.KC.Enterprises.dto.SchoolPurchaseOrderResponse;
import com.KC.Enterprises.dto.SchoolPurchaseOrderSummaryResponse;

public interface SchoolPurchaseOrderService {
    
    SchoolPurchaseOrderResponse createPurchaseOrder(SchoolPurchaseOrderRequest request);
    
    SchoolPurchaseOrderResponse getPurchaseOrderById(Long id);
    
    List<SchoolPurchaseOrderSummaryResponse> getAllPurchaseOrders();
    
    List<SchoolPurchaseOrderSummaryResponse> getPurchaseOrdersBySchool(Long schoolId);
    
    List<SchoolPurchaseOrderSummaryResponse> getPurchaseOrdersByStatus(String status);
    
    List<SchoolPurchaseOrderSummaryResponse> getPurchaseOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    SchoolPurchaseOrderResponse updatePurchaseOrder(Long id, SchoolPurchaseOrderRequest request);
    
    SchoolPurchaseOrderResponse updatePurchaseOrderStatus(Long id, String status);
    
    DeleteResponse deletePurchaseOrder(Long id);
    
    SchoolPurchaseOrderResponse addItemToPurchaseOrder(Long orderId, SchoolPurchaseOrderItemRequest itemRequest);
    
    SchoolPurchaseOrderResponse removeItemFromPurchaseOrder(Long orderId, Long itemId);
    
    SchoolPurchaseOrderResponse updateItemFinalSellingPrice(Long orderId, Long itemId, Double finalSellingPrice);
    
    SchoolPurchaseOrderResponse updateItemDiscountPercentage(Long orderId, Long itemId, Double discountPercentage);
    
    List<SchoolPurchaseOrderSummaryResponse> getOverdueOrders();
    
    Double getTotalPurchaseAmountBySchool(Long schoolId);
    
    DiscountCalculationResponse calculateDiscount(Long productId, Double finalSellingPrice, Double discountPercentage);
    
    SchoolOrderStatisticsResponse getSchoolOrderStatistics(Long schoolId);
    
    SchoolPurchaseOrderResponse updateItemQuantity(Long orderId, Long itemId, Integer quantity);
}
