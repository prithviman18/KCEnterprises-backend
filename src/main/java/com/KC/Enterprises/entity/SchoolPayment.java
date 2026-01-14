package com.KC.Enterprises.entity;

import java.time.LocalDateTime;

import com.KC.Enterprises.enums.PaymentMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "school_payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchoolPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private SchoolPurchaseOrder purchaseOrder;

    @Column(nullable = false)
    private Double amountPaid;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String transactionReference;

    private String remarks;

    private LocalDateTime createdAt = LocalDateTime.now();
}
