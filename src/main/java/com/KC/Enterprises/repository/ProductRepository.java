package com.KC.Enterprises.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.KC.Enterprises.entity.Product;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findBySupplierId(Long supplierId);

    List<Product> findByCategoryIgnoreCase(String category);

    @Query("SELECT p FROM Product p WHERE LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Product> searchByCategory(@Param("category") String category);
}
