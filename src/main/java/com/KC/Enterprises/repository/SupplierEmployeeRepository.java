package com.KC.Enterprises.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.KC.Enterprises.entity.SupplierEmployees;

public interface SupplierEmployeeRepository extends JpaRepository<SupplierEmployees,Long>{
    List<SupplierEmployees> findBySupplierId(Long supplierId);
    
}
