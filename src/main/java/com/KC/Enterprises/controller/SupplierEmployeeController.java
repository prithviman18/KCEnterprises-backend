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
import com.KC.Enterprises.dto.SupplierEmployeeRequest;
import com.KC.Enterprises.dto.SupplierEmployeeResponse;
import com.KC.Enterprises.service.SupplierEmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/supplier")
@RequiredArgsConstructor
@Tag(name = "Supplier Employee", description = "Supplier management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class SupplierEmployeeController {
    private final SupplierEmployeeService supplierEmployeeService;

    @PostMapping("/{supplierId}/employees")
    @Operation(summary = "Create new employee for supplier")
    public ResponseEntity<ApiResponse<SupplierEmployeeResponse>> createSupplierEmployee(
            @PathVariable Long supplierId,
            @Valid @RequestBody SupplierEmployeeRequest request) {
        
        request.setSupplierId(supplierId);
        
        SupplierEmployeeResponse response = supplierEmployeeService.createEmployee(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Supplier employee created successfully"));
    }

    @GetMapping("/{supplierId}/employees")
    @Operation(summary = "Get all employees by supplier ID")
    public ResponseEntity<ApiResponse<List<SupplierEmployeeResponse>>> getEmployeesBySupplier(
            @PathVariable Long supplierId) {
        List<SupplierEmployeeResponse> employees = supplierEmployeeService.getEmployeesBySupplier(supplierId);
        return ResponseEntity.ok(ApiResponse.success(employees, "Supplier employees retrieved successfully"));
    }
    
    @GetMapping("/employees/{employeeId}")
    @Operation(summary = "Get supplier employee by employee ID")
    public ResponseEntity<ApiResponse<SupplierEmployeeResponse>> getEmployeeById(
            @PathVariable Long employeeId) {
        SupplierEmployeeResponse response = supplierEmployeeService.getEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success(response, "Supplier employee retrieved successfully"));
    }

    @PutMapping("/employees/{employeeId}")
    @Operation(summary = "Update an employee")
    public ResponseEntity<ApiResponse<SupplierEmployeeResponse>> updateSupplierEmployee(
        @PathVariable Long employeeId, 
        @RequestBody SupplierEmployeeRequest request
    ) {
            SupplierEmployeeResponse response = supplierEmployeeService.updateEmployee(employeeId,request);
            return ResponseEntity.ok(ApiResponse.success(response,"Employee updated successfully"));
        }
    
    @DeleteMapping("/employees/{employeeId}")
    @Operation(summary = "Delete supplier employee by employee ID")
    public ResponseEntity<ApiResponse<DeleteResponse>> deleteEmployee(
            @PathVariable Long employeeId) {
        DeleteResponse response = supplierEmployeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success(response, "Supplier employee deleted successfully"));
    }


}
