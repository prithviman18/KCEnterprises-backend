package com.KC.Enterprises.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.SupplierRequest;
import com.KC.Enterprises.dto.SupplierResponse;
import com.KC.Enterprises.entity.Supplier;
import com.KC.Enterprises.exception.ResourceNotFoundException;
import com.KC.Enterprises.repository.SupplierRepository;
import com.KC.Enterprises.service.SupplierService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;

    @Override
    public SupplierResponse createSupplier(SupplierRequest request){
        Supplier supplier = Supplier.builder()
            .supplierName(request.getSupplierName())
            .email(request.getEmail())
            .mobile(request.getMobile())
            .address(request.getAddress())
            .contactPerson(request.getContactPerson())
            .build();

        Supplier saved = supplierRepository.save(supplier);

        return SupplierResponse.builder()
                .id(saved.getId())
                .supplierName(saved.getSupplierName())
                .email(saved.getEmail())
                .mobile(saved.getMobile())
                .address(saved.getAddress())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        
        return mapToSupplierResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll()
            .stream()
            .map(this::mapToSupplierResponse)
            .collect(Collectors.toList());
    }

    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier existingSupplier = supplierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        existingSupplier.setSupplierName(request.getSupplierName());
        existingSupplier.setEmail(request.getEmail());
        existingSupplier.setMobile(request.getMobile());
        existingSupplier.setAddress(request.getAddress());
        existingSupplier.setContactPerson(request.getContactPerson());

        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return mapToSupplierResponse(updatedSupplier);
    }

    @Override
    public DeleteResponse deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + id);
        }
    
        supplierRepository.deleteById(id);
    
        return DeleteResponse.builder()
                .success(true)
                .message("Supplier deleted successfully")
                .deletedId(id)
                .build();
    }


     // Helper method to convert Entity to Response DTO
     private SupplierResponse mapToSupplierResponse(Supplier supplier) {
        return SupplierResponse.builder()
            .id(supplier.getId())
            .supplierName(supplier.getSupplierName())
            .email(supplier.getEmail())
            .mobile(supplier.getMobile())
            .address(supplier.getAddress())
            .contactPerson(supplier.getContactPerson())
            .createdAt(supplier.getCreatedAt())
            .updatedAt(supplier.getUpdatedAt())
            .build();
    }

    
}
