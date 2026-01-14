package com.KC.Enterprises.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.KC.Enterprises.entity.ProductPriceHistory;

public interface ProductPriceHistoryRepository extends JpaRepository<ProductPriceHistory, Long> {
    List<ProductPriceHistory> findByProductId(Long productId);
}
