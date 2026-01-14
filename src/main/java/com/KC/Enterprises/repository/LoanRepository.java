package com.KC.Enterprises.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.KC.Enterprises.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    
}
