package com.KC.Enterprises.controller;

import com.KC.Enterprises.dto.ApiResponse;
import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.StockRequest;
import com.KC.Enterprises.dto.StockResponse;
import com.KC.Enterprises.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Inventory stock management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class StockController {

    private final StockService stockService;

    @PostMapping
    @Operation(summary = "Create stock entry for a product")
    public ResponseEntity<ApiResponse<StockResponse>> createStock(@RequestBody StockRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                stockService.createStock(request), "Stock created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all stock entries")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getAllStock() {
        return ResponseEntity.ok(ApiResponse.success(
                stockService.getAllStock(), "Stock retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get stock by ID")
    public ResponseEntity<ApiResponse<StockResponse>> getStockById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                stockService.getStockById(id), "Stock retrieved successfully"));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get stock by product ID")
    public ResponseEntity<ApiResponse<StockResponse>> getStockByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(
                stockService.getStockByProductId(productId), "Stock retrieved successfully"));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get all low stock items")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.success(
                stockService.getLowStock(), "Low stock items retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update stock entry")
    public ResponseEntity<ApiResponse<StockResponse>> updateStock(
            @PathVariable Long id, @RequestBody StockRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                stockService.updateStock(id, request), "Stock updated successfully"));
    }

    @PatchMapping("/product/{productId}/adjust")
    @Operation(summary = "Adjust stock quantity (positive = add, negative = remove)")
    public ResponseEntity<ApiResponse<StockResponse>> adjustQuantity(
            @PathVariable Long productId,
            @RequestParam Integer delta) {
        return ResponseEntity.ok(ApiResponse.success(
                stockService.adjustQuantity(productId, delta), "Stock quantity adjusted"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete stock entry")
    public ResponseEntity<ApiResponse<DeleteResponse>> deleteStock(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                stockService.deleteStock(id), "Stock deleted successfully"));
    }
}