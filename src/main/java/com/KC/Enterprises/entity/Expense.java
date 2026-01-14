package com.KC.Enterprises.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.KC.Enterprises.enums.ExpenseType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expenses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

    private Double amount;

    private String description;

    @CreationTimestamp
    private LocalDateTime expenseDate;
}
