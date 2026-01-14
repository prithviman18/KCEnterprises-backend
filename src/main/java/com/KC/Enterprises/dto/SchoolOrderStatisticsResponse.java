package com.KC.Enterprises.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolOrderStatisticsResponse {
    private Long schoolId;
    private String schoolName;
    private Integer totalOrders;
    private Double totalPurchaseAmount;
    private Double averageOrderValue;
    private Integer pendingOrders;
    private Integer deliveredOrders;
    private Integer cancelledOrders;
}
