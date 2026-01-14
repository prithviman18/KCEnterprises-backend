package com.KC.Enterprises.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class DiscountCalculationResponse {
        private Long productId;
        private String productName;
        private Double mrp;
        private Double finalSellingPrice;
        private Double discountPercentage;
        private Double discountAmount;
    }