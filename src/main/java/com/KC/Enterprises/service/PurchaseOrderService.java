package com.KC.Enterprises.service;

import java.time.LocalDate;
import java.util.List;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.PurchaseOrderItemRequest;
import com.KC.Enterprises.dto.PurchaseOrderItemqtyRequest;
import com.KC.Enterprises.dto.PurchaseOrderRequest;
import com.KC.Enterprises.dto.PurchaseOrderResponse;
import com.KC.Enterprises.dto.PurchaseOrderSummaryResponse;

public interface PurchaseOrderService {
    PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request);
    
    PurchaseOrderResponse getPurchaseOrderById(Long id);

    List<PurchaseOrderSummaryResponse> getAllPurchaseOrders();

    List<PurchaseOrderSummaryResponse> getPurchaseOrdersBySupplier(Long supplierId);

    List<PurchaseOrderSummaryResponse> getPurchaseOrdersByStatus(String status);

    List<PurchaseOrderSummaryResponse> getPurchaseOrdersByDateRange(LocalDate startDate, LocalDate endDate);

    PurchaseOrderResponse updatePurchaseOrder(Long id, PurchaseOrderRequest request);

    PurchaseOrderResponse updatePurchaseOrderStatus(Long id, String status);

    DeleteResponse deletePurchaseOrder(Long id);

    PurchaseOrderResponse addItemToPurchaseOrder(Long orderId, PurchaseOrderItemRequest itemRequest);

    PurchaseOrderResponse removeItemFromPurchaseOrder(Long orderId, Long itemId);

    PurchaseOrderResponse updateItemQuantity(Long orderId, Long itemId, PurchaseOrderItemqtyRequest request);
}
