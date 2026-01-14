package com.KC.Enterprises.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "school_purchase_order_item")
public class SchoolPurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private SchoolPurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double finalSellingPrice;

    @Column(nullable = false)
    private Double totalAmount;

    // Transient fields for discount calculation (not stored in DB)
    @Transient
    private Double mrp;
    
    @Transient
    private Double discountPercentage;
    
    @Transient
    private Double discountAmount;

    @PrePersist
    @PreUpdate
    public void calculateAmounts() {
        // Always get MRP from product
        if (this.product != null) {
            this.mrp = this.product.getMrp();
        }
        
        // Calculate discount based on finalSellingPrice and MRP
        if (this.finalSellingPrice != null && this.mrp != null && this.mrp > 0) {
            this.discountAmount = this.mrp - this.finalSellingPrice;
            this.discountPercentage = (this.discountAmount / this.mrp) * 100;
            
            // Round to 2 decimal places
            this.discountAmount = Math.round(this.discountAmount * 100.0) / 100.0;
            this.discountPercentage = Math.round(this.discountPercentage * 100.0) / 100.0;
        }
        
        // Calculate total amount
        if (this.finalSellingPrice != null && this.quantity != null) {
            this.totalAmount = this.finalSellingPrice * this.quantity;
            this.totalAmount = Math.round(this.totalAmount * 100.0) / 100.0;
        }
    }

    // Helper method to validate discount
    public void validateDiscount() {
        if (this.mrp == null && this.product != null) {
            this.mrp = this.product.getMrp();
        }
        
        if (this.mrp == null) {
            throw new IllegalArgumentException("Product MRP cannot be determined");
        }
        
        if (this.finalSellingPrice != null && this.finalSellingPrice > this.mrp) {
            throw new IllegalArgumentException(
                String.format("Final selling price (%.2f) cannot be greater than MRP (%.2f)", 
                    this.finalSellingPrice, this.mrp));
        }
        
        if (this.finalSellingPrice != null && this.finalSellingPrice < 0) {
            throw new IllegalArgumentException("Final selling price cannot be negative");
        }
        
        // Calculate discount to validate it's reasonable
        if (this.finalSellingPrice != null && this.mrp != null) {
            double calculatedDiscount = ((this.mrp - this.finalSellingPrice) / this.mrp) * 100;
            if (calculatedDiscount > 100) {
                throw new IllegalArgumentException("Discount cannot exceed 100%");
            }
        }
    }

    // Helper method to set price with discount percentage
    public void setPriceWithDiscountPercentage(Double discountPercentage) {
        if (discountPercentage == null || this.mrp == null) {
            return;
        }
        
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        
        this.discountPercentage = discountPercentage;
        this.discountAmount = this.mrp * (discountPercentage / 100);
        this.finalSellingPrice = this.mrp - this.discountAmount;
        
        // Round to 2 decimal places
        this.finalSellingPrice = Math.round(this.finalSellingPrice * 100.0) / 100.0;
        this.discountAmount = Math.round(this.discountAmount * 100.0) / 100.0;
    }

    // Helper method to set price directly
    public void setFinalSellingPrice(Double price) {
        this.finalSellingPrice = price;
        calculateAmounts(); // Recalculate discount
    }
}