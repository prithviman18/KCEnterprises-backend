package com.KC.Enterprises.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.KC.Enterprises.dto.ApiResponse;
import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.PurchaseOrderItemRequest;
import com.KC.Enterprises.dto.PurchaseOrderItemqtyRequest;
import com.KC.Enterprises.dto.PurchaseOrderRequest;
import com.KC.Enterprises.dto.PurchaseOrderResponse;
import com.KC.Enterprises.dto.PurchaseOrderSummaryResponse;
import com.KC.Enterprises.service.PurchaseOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Purchase Orders", description = "Purchase order management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class PurchaseOrderController {
    
    private final PurchaseOrderService purchaseOrderService;
    
    @PostMapping
    @Operation(summary = "Create a new purchase order")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrderResponse response = purchaseOrderService.createPurchaseOrder(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Purchase order created successfully"));
    }
    
    @GetMapping
    @Operation(summary = "Get all purchase orders")
    public ResponseEntity<ApiResponse<List<PurchaseOrderSummaryResponse>>> getAllPurchaseOrders() {
        List<PurchaseOrderSummaryResponse> orders = purchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(ApiResponse.success(orders, "Purchase orders retrieved successfully"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get purchase order by ID")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> getPurchaseOrderById(
            @PathVariable Long id) {
        PurchaseOrderResponse order = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order, "Purchase order retrieved successfully"));
    }
    
    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Get purchase orders by supplier ID")
    public ResponseEntity<ApiResponse<List<PurchaseOrderSummaryResponse>>> getPurchaseOrdersBySupplier(
            @PathVariable Long supplierId) {
        List<PurchaseOrderSummaryResponse> orders = purchaseOrderService.getPurchaseOrdersBySupplier(supplierId);
        return ResponseEntity.ok(ApiResponse.success(orders, "Purchase orders retrieved successfully"));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get purchase orders by status")
    public ResponseEntity<ApiResponse<List<PurchaseOrderSummaryResponse>>> getPurchaseOrdersByStatus(
            @PathVariable String status) {
        List<PurchaseOrderSummaryResponse> orders = purchaseOrderService.getPurchaseOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(orders, "Purchase orders retrieved successfully"));
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Get purchase orders by date range")
    public ResponseEntity<ApiResponse<List<PurchaseOrderSummaryResponse>>> getPurchaseOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PurchaseOrderSummaryResponse> orders = purchaseOrderService
                .getPurchaseOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(orders, "Purchase orders retrieved successfully"));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update purchase order")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> updatePurchaseOrder(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrderResponse response = purchaseOrderService.updatePurchaseOrder(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Purchase order updated successfully"));
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update purchase order status")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> updatePurchaseOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        PurchaseOrderResponse response = purchaseOrderService.updatePurchaseOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Purchase order status updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete purchase order")
    public ResponseEntity<ApiResponse<DeleteResponse>> deletePurchaseOrder(@PathVariable Long id) {
        DeleteResponse response = purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Purchase order deleted successfully"));
    }
    
    @PostMapping("/{orderId}/items")
    @Operation(summary = "Add item to purchase order")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> addItemToPurchaseOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody PurchaseOrderItemRequest itemRequest) {
        PurchaseOrderResponse response = purchaseOrderService.addItemToPurchaseOrder(orderId, itemRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Item added to purchase order successfully"));
    }
    
    @DeleteMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Remove item from purchase order")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> removeItemFromPurchaseOrder(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {
        PurchaseOrderResponse response = purchaseOrderService.removeItemFromPurchaseOrder(orderId, itemId);
        return ResponseEntity.ok(ApiResponse.success(response, "Item removed from purchase order successfully"));
    }

    @PutMapping("/{orderId}/items/{itemId}/quantity")
    @Operation(summary = "Update item quantity in purchase order")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> updateItemQuantity(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @Valid @RequestBody PurchaseOrderItemqtyRequest request) {
            PurchaseOrderResponse response = purchaseOrderService.updateItemQuantity(orderId, itemId, request);
            return ResponseEntity.ok(ApiResponse.success(response, "Item quantity updated successfully"));
    }
}