package com.KC.Enterprises.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.KC.Enterprises.enums.SchoolOrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolPurchaseOrderResponse {
    
    private Long id;
    private Long schoolId;
    private String schoolName;
    private String schoolCode;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime deliveryDate;
    private SchoolOrderStatus status;
    private Double totalAmount;
    private Double totalDiscount;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SchoolPurchaseOrderItemResponse> items;

}
