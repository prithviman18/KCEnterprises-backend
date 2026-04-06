package com.KC.Enterprises.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.KC.Enterprises.entity.Stock;

public interface StockRepository extends JpaRepository<Stock,Long>{
    Optional<Stock> findByProductId(Long productId);
}
