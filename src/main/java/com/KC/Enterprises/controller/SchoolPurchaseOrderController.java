package com.KC.Enterprises.controller;

import java.time.LocalDateTime;
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
import com.KC.Enterprises.dto.DiscountCalculationResponse;
import com.KC.Enterprises.dto.SchoolOrderStatisticsResponse;
import com.KC.Enterprises.dto.SchoolPurchaseOrderItemRequest;
import com.KC.Enterprises.dto.SchoolPurchaseOrderRequest;
import com.KC.Enterprises.dto.SchoolPurchaseOrderResponse;
import com.KC.Enterprises.dto.SchoolPurchaseOrderSummaryResponse;
import com.KC.Enterprises.service.SchoolPurchaseOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/school-purchase-orders")
@RequiredArgsConstructor
@Tag(name = "School Purchase Orders", description = "School purchase order management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class SchoolPurchaseOrderController {
    
    private final SchoolPurchaseOrderService schoolPurchaseOrderService;
    
    @PostMapping
    @Operation(summary = "Create a new school purchase order")
    public ResponseEntity<ApiResponse<SchoolPurchaseOrderResponse>> createPurchaseOrder(
            @Valid @RequestBody SchoolPurchaseOrderRequest request) {
        SchoolPurchaseOrderResponse response = schoolPurchaseOrderService.createPurchaseOrder(request);
        return ResponseEntity.ok(ApiResponse.success(response, "School purchase order created successfully"));
    }
    
    @GetMapping
    @Operation(summary = "Get all school purchase orders")
    public ResponseEntity<ApiResponse<List<SchoolPurchaseOrderSummaryResponse>>> getAllPurchaseOrders() {
        List<SchoolPurchaseOrderSummaryResponse> orders = schoolPurchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(ApiResponse.success(orders, "School purchase orders retrieved successfully"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get school purchase order by ID")
    public ResponseEntity<ApiResponse<SchoolPurchaseOrderResponse>> getPurchaseOrderById(
            @PathVariable Long id) {
        SchoolPurchaseOrderResponse order = schoolPurchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order, "School purchase order retrieved successfully"));
    }
    
    @GetMapping("/school/{schoolId}")
    @Operation(summary = "Get school purchase orders by school ID")
    public ResponseEntity<ApiResponse<List<SchoolPurchaseOrderSummaryResponse>>> getPurchaseOrdersBySchool(
            @PathVariable Long schoolId) {
        List<SchoolPurchaseOrderSummaryResponse> orders = schoolPurchaseOrderService.getPurchaseOrdersBySchool(schoolId);
        return ResponseEntity.ok(ApiResponse.success(orders, "School purchase orders retrieved successfully"));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get school purchase orders by status")
    public ResponseEntity<ApiResponse<List<SchoolPurchaseOrderSummaryResponse>>> getPurchaseOrdersByStatus(
            @PathVariable String status) {
        List<SchoolPurchaseOrderSummaryResponse> orders = schoolPurchaseOrderService.getPurchaseOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(orders, "School purchase orders retrieved successfully"));
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Get school purchase orders by date range")
    public ResponseEntity<ApiResponse<List<SchoolPurchaseOrderSummaryResponse>>> getPurchaseOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<SchoolPurchaseOrderSummaryResponse> orders = schoolPurchaseOrderService
                .getPurchaseOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(orders, "School purchase orders retrieved successfully"));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update school purchase order")
    public ResponseEntity<ApiResponse<SchoolPurchaseOrderResponse>> updatePurchaseOrder(
            @PathVariable Long id,
            @Valid @RequestBody SchoolPurchaseOrderRequest request) {
        SchoolPurchaseOrderResponse response = schoolPurchaseOrderService.updatePurchaseOrder(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "School purchase order updated successfully"));
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update school purchase order status")
    public ResponseEntity<ApiResponse<SchoolPurchaseOrderResponse>> updatePurchaseOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        SchoolPurchaseOrderResponse response = schoolPurchaseOrderService.updatePurchaseOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(response, "School purchase order status updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete school purchase order")
    public ResponseEntity<ApiResponse<DeleteResponse>> deletePurchaseOrder(@PathVariable Long id) {
        DeleteResponse response = schoolPurchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.success(response, "School purchase order deleted successfully"));
    }
    
    @PostMapping("/{orderId}/items")
    @Operation(summary = "Add item to school purchase order")
    public ResponseEntity<ApiResponse<SchoolPurchaseOrderResponse>> addItemToPurchaseOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody SchoolPurchaseOrderItemRequest itemRequest) {
        SchoolPurchaseOrderResponse response = schoolPurchaseOrderService.addItemToPurchaseOrder(orderId, itemRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Item added to school purchase order successfully"));
    }
    
    @DeleteMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Remove item from school purchase order")
    public ResponseEntity<ApiResponse<SchoolPurchaseOrderResponse>> removeItemFromPurchaseOrder(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {
        SchoolPurchaseOrderResponse response = schoolPurchaseOrderService.removeItemFromPurchaseOrder(orderId, itemId);
        return ResponseEntity.ok(ApiResponse.success(response, "Item removed from school purchase order successfully"));
    }
    
    @PutMapping("/{orderId}/items/{itemId}/price")
    @Operation(summary = "Update item final selling price in school purchase order")
    public ResponseEntity<ApiResponse<SchoolPurchaseOrderResponse>> updateItemFinalSellingPrice(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestParam Double finalSellingPrice) {
        SchoolPurchaseOrderResponse response = schoolPurchaseOrderService
                .updateItemFinalSellingPrice(orderId, itemId, finalSellingPrice);
        return ResponseEntity.ok(ApiResponse.success(response, "Item price updated successfully"));
    }
    
    @PutMapping("/{orderId}/items/{itemId}/discount")
    @Operation(summary = "Update item discount percentage in school purchase order")
    public ResponseEntity<ApiResponse<SchoolPurchaseOrderResponse>> updateItemDiscountPercentage(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestParam Double discountPercentage) {
        SchoolPurchaseOrderResponse response = schoolPurchaseOrderService
                .updateItemDiscountPercentage(orderId, itemId, discountPercentage);
        return ResponseEntity.ok(ApiResponse.success(response, "Item discount updated successfully"));
    }
    
    @PutMapping("/{orderId}/items/{itemId}/quantity")
    @Operation(summary = "Update item quantity in school purchase order")
    public ResponseEntity<ApiResponse<SchoolPurchaseOrderResponse>> updateItemQuantity(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        SchoolPurchaseOrderResponse response = schoolPurchaseOrderService
                .updateItemQuantity(orderId, itemId, quantity);
        return ResponseEntity.ok(ApiResponse.success(response, "Item quantity updated successfully"));
    }
    
    @GetMapping("/overdue")
    @Operation(summary = "Get overdue school purchase orders")
    public ResponseEntity<ApiResponse<List<SchoolPurchaseOrderSummaryResponse>>> getOverdueOrders() {
        List<SchoolPurchaseOrderSummaryResponse> orders = schoolPurchaseOrderService.getOverdueOrders();
        return ResponseEntity.ok(ApiResponse.success(orders, "Overdue school purchase orders retrieved successfully"));
    }
    
    @GetMapping("/school/{schoolId}/total-amount")
    @Operation(summary = "Get total purchase amount by school")
    public ResponseEntity<ApiResponse<Double>> getTotalPurchaseAmountBySchool(
            @PathVariable Long schoolId) {
        Double totalAmount = schoolPurchaseOrderService.getTotalPurchaseAmountBySchool(schoolId);
        return ResponseEntity.ok(ApiResponse.success(totalAmount, "Total purchase amount retrieved successfully"));
    }
    
    @GetMapping("/calculate-discount")
    @Operation(summary = "Calculate discount for a product")
    public ResponseEntity<ApiResponse<DiscountCalculationResponse>> calculateDiscount(
            @RequestParam Long productId,
            @RequestParam(required = false) Double finalSellingPrice,
            @RequestParam(required = false) Double discountPercentage) {
        DiscountCalculationResponse response = schoolPurchaseOrderService
                .calculateDiscount(productId, finalSellingPrice, discountPercentage);
        return ResponseEntity.ok(ApiResponse.success(response, "Discount calculated successfully"));
    }
    
    @GetMapping("/school/{schoolId}/statistics")
    @Operation(summary = "Get order statistics for a school")
    public ResponseEntity<ApiResponse<SchoolOrderStatisticsResponse>> getSchoolOrderStatistics(
            @PathVariable Long schoolId) {
        SchoolOrderStatisticsResponse statistics = schoolPurchaseOrderService.getSchoolOrderStatistics(schoolId);
        return ResponseEntity.ok(ApiResponse.success(statistics, "School order statistics retrieved successfully"));
    }
}