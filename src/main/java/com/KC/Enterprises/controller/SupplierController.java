package com.KC.Enterprises.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.KC.Enterprises.dto.ApiResponse;
import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.SupplierRequest;
import com.KC.Enterprises.dto.SupplierResponse;
import com.KC.Enterprises.service.SupplierService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/supplier")
@RequiredArgsConstructor
@Tag(name = "Supplier", description = "Supplier management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class SupplierController {
    private final SupplierService supplierService;
    
    @PostMapping()
    @Operation(summary = "Create new supplier")
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(
            @Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.createSupplier(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Supplier created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all suppliers")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getAllSuppliers() {
        List<SupplierResponse> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(ApiResponse.success(suppliers, "Suppliers retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(@PathVariable Long id) {
        SupplierResponse supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(ApiResponse.success(supplier, "Supplier retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update supplier")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @PathVariable Long id, 
            @Valid @RequestBody SupplierRequest request) {
        SupplierResponse supplier = supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(ApiResponse.success(supplier, "Supplier updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supplier")
    public ResponseEntity<ApiResponse<DeleteResponse>> deleteSupplier(@PathVariable Long id) {
        DeleteResponse deleteResponse = supplierService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.success(deleteResponse, "Supplier deleted successfully"));
    }
}