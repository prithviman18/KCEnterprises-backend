package com.KC.Enterprises.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolPurchaseOrderItemRequest {
    
    private Long productId;
    private Integer quantity;
    private Double finalSellingPrice;
    private Double discountPercentage; 
}