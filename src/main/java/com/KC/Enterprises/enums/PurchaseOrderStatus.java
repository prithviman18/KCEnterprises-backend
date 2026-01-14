package com.KC.Enterprises.enums;

public enum PurchaseOrderStatus {
    CREATED,        // PO created but not yet sent to supplier
    SENT,           // Sent to supplier, waiting for confirmation
    CONFIRMED,      // Supplier has confirmed the order
    PARTIALLY_RECEIVED, // Some items delivered
    DELIVERED,      // Fully delivered
    CANCELLED       // Order cancelled
}
