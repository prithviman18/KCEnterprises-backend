package com.KC.Enterprises.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolPurchaseOrderItemResponse {
    
    private Long id;
    private Long productId;
    private String productName;
    private String productCategory;
    private String brand;
    private String unitOfMeasure;
    private Integer quantity;
    private Double mrp;
    private Double finalSellingPrice;  
    private Double discountPercentage; 
    private Double discountAmount;   
    private Double totalAmount;
}
