package com.KC.Enterprises.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.KC.Enterprises.enums.SchoolOrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolPurchaseOrderRequest {
    
    @NotNull(message = "School ID is required")
    private Long schoolId;
    
    @NotNull(message = "Order date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime orderDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime expectedDeliveryDate;
    
    private SchoolOrderStatus status;
    
    private String remarks;
    
    @NotEmpty(message = "At least one item is required")
    private List<SchoolPurchaseOrderItemRequest> items;
}