package com.KC.Enterprises.service;

import java.util.List;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.SupplierRequest;
import com.KC.Enterprises.dto.SupplierResponse;

public interface SupplierService {
    SupplierResponse createSupplier(SupplierRequest request);
    SupplierResponse getSupplierById(Long id);
    List<SupplierResponse> getAllSuppliers();
    SupplierResponse updateSupplier(Long id, SupplierRequest request);
    DeleteResponse deleteSupplier(Long id);
}
