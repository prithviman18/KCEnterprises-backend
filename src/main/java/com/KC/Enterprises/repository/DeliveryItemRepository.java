package com.KC.Enterprises.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.KC.Enterprises.entity.DeliveryItem;

public interface DeliveryItemRepository extends JpaRepository<DeliveryItem,Long> {
    
}
