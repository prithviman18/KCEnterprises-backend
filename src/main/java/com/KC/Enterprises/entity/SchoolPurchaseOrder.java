package com.KC.Enterprises.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.KC.Enterprises.enums.SchoolOrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@Entity
@AllArgsConstructor

@Table(name = "school_purchase_order")
public class SchoolPurchaseOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @OneToMany(
        mappedBy = "purchaseOrder", 
        cascade = CascadeType.ALL, 
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    private List<SchoolPurchaseOrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = true)
    private LocalDateTime deliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SchoolOrderStatus status;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private Double totalDiscount;

    @Column(length = 500)
    private String remarks;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public SchoolPurchaseOrder() {
        this.totalAmount = 0.0;
        this.totalDiscount = 0.0;
        this.status = SchoolOrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    public void onCreate() {
        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = SchoolOrderStatus.CREATED;
        }
        if (this.totalAmount == null) {
            this.totalAmount = 0.0;
        }
        if (this.totalDiscount == null) {
            this.totalDiscount = 0.0;
        }
    }


    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods to manage bidirectional relationship
    public void addItem(SchoolPurchaseOrderItem item) {
        items.add(item);
        item.setPurchaseOrder(this);
    }

    public void removeItem(SchoolPurchaseOrderItem item) {
        items.remove(item);
        item.setPurchaseOrder(null);
    }

    // Helper method to calculate total amounts
    public void calculateTotals() {
        if (items != null && !items.isEmpty()) {
            this.totalAmount = items.stream()
                .mapToDouble(SchoolPurchaseOrderItem::getTotalAmount)
                .sum();
            
            this.totalDiscount = items.stream()
                .mapToDouble(item -> {
                    if (item.getProduct() != null) {
                        Double mrp = item.getProduct().getMrp();
                        Double finalPrice = item.getFinalSellingPrice();
                        if (mrp != null && finalPrice != null) {
                            return (mrp - finalPrice) * item.getQuantity();
                        }
                    }
                    return 0.0;
                })
                .sum();
            
            // Round to 2 decimal places
            this.totalAmount = Math.round(this.totalAmount * 100.0) / 100.0;
            this.totalDiscount = Math.round(this.totalDiscount * 100.0) / 100.0;
        } else {
            this.totalAmount = 0.0;
            this.totalDiscount = 0.0;
        }
    }
}