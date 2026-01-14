package com.KC.Enterprises.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.KC.Enterprises.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long>{
    
}
