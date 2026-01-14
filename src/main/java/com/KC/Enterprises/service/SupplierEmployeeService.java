package com.KC.Enterprises.service;

import java.util.List;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.SupplierEmployeeRequest;
import com.KC.Enterprises.dto.SupplierEmployeeResponse;


public interface SupplierEmployeeService {
    SupplierEmployeeResponse createEmployee(SupplierEmployeeRequest dto);

    SupplierEmployeeResponse updateEmployee(Long id, SupplierEmployeeRequest dto);

    SupplierEmployeeResponse getEmployee(Long id);

    List<SupplierEmployeeResponse> getEmployeesBySupplier(Long supplierId);

    DeleteResponse deleteEmployee(Long id);
}
