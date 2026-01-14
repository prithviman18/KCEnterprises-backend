package com.KC.Enterprises.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.KC.Enterprises.entity.LoanPayment;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Long> {
    
}
