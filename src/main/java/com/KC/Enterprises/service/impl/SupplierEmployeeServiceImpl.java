package com.KC.Enterprises.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.SupplierEmployeeRequest;
import com.KC.Enterprises.dto.SupplierEmployeeResponse;
import com.KC.Enterprises.entity.Supplier;
import com.KC.Enterprises.entity.SupplierEmployees;
import com.KC.Enterprises.exception.ResourceNotFoundException;
import com.KC.Enterprises.repository.SupplierEmployeeRepository;
import com.KC.Enterprises.repository.SupplierRepository;
import com.KC.Enterprises.service.SupplierEmployeeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierEmployeeServiceImpl implements SupplierEmployeeService {

    private final SupplierEmployeeRepository employeeRepo;
    private final SupplierRepository supplierRepo;

    @Override
    public SupplierEmployeeResponse createEmployee(SupplierEmployeeRequest dto) {

        Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        SupplierEmployees emp = new SupplierEmployees();
        emp.setEmployeeName(dto.getEmployeeName());
        emp.setPhone(dto.getPhone());
        emp.setDesignation(dto.getDesignation());
        emp.setEmail(dto.getEmail());
        emp.setSupplier(supplier);

        employeeRepo.save(emp);

        return mapToResponse(emp);
    }

    @Override
    public SupplierEmployeeResponse updateEmployee(Long id, SupplierEmployeeRequest dto) {
        
        SupplierEmployees emp = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        emp.setEmployeeName(dto.getEmployeeName());
        emp.setPhone(dto.getPhone());
        emp.setDesignation(dto.getDesignation());
        emp.setEmail(dto.getEmail());
        emp.setSupplier(supplier);

        employeeRepo.save(emp);

        return mapToResponse(emp);
    }

    @Override
    public SupplierEmployeeResponse getEmployee(Long id) {
        SupplierEmployees emp = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToResponse(emp);
    }

    @Override
    public List<SupplierEmployeeResponse> getEmployeesBySupplier(Long supplierId) {
        return employeeRepo.findAll()
                .stream()
                .filter(e -> e.getSupplier().getId().equals(supplierId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeleteResponse deleteEmployee(Long id) {
        if (!employeeRepo.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + id);
        }
    
        employeeRepo.deleteById(id);
    
        return DeleteResponse.builder()
                .success(true)
                .message("Supplier deleted successfully")
                .deletedId(id)
                .build();
    }
    

    private SupplierEmployeeResponse mapToResponse(SupplierEmployees emp) {
        return SupplierEmployeeResponse.builder()
                .id(emp.getId())
                .employeeName(emp.getEmployeeName())
                .phone(emp.getPhone())
                .designation(emp.getDesignation())
                .email(emp.getEmail())
                .supplierId(emp.getSupplier().getId())
                .supplierName(emp.getSupplier().getSupplierName())
                .createdAt(emp.getCreatedAt())
                .updatedAt(emp.getUpdatedAt())
                .build();
    }
}
