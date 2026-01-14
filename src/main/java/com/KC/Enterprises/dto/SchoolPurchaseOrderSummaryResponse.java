package com.KC.Enterprises.dto;

import java.time.LocalDateTime;

import com.KC.Enterprises.enums.SchoolOrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolPurchaseOrderSummaryResponse {
    private Long id;
        private Long schoolId;
        private String schoolName;
        private LocalDateTime orderDate;
        private LocalDateTime expectedDeliveryDate;
        private SchoolOrderStatus status;
        private Double totalAmount;
        private Double totalDiscount;
        private Integer totalItems;
        private LocalDateTime createdAt;
}
