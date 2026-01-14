package com.KC.Enterprises.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.KC.Enterprises.entity.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}