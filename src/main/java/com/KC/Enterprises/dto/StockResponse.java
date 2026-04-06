package com.KC.Enterprises.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class StockResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String category;
    private String brand;
    private Double mrp;
    private Integer quantity;
    private Integer minimumQuantity;
    private Boolean isLowStock;
    private LocalDateTime updatedAt;
}