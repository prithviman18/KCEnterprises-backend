package com.KC.Enterprises.dto;

import lombok.Data;

@Data
public class SupplierRequest {
    private String supplierName;
    private String email;
    private String mobile;
    private String address;
    private String contactPerson;
}
