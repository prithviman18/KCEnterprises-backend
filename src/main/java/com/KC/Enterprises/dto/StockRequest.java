package com.KC.Enterprises.dto;

import lombok.Data;

@Data
public class StockRequest {
    private Long productId;
    private Integer quantity;
    private Integer minimumQuantity;
}