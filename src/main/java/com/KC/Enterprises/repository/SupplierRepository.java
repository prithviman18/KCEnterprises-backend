package com.KC.Enterprises.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.KC.Enterprises.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}