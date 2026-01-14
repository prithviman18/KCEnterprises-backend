package com.KC.Enterprises.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemResponse {
    
    private Long id;
    private Long productId;
    private String productName;
    private String productCategory;
    private Integer quantity;
    private Double costPrice;
    private Double totalPrice;
}