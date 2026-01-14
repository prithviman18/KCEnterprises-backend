package com.KC.Enterprises.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchoolRequest {
    @NotBlank(message = "School name is required")
    private String schoolName;
    
    private String address;
    
    private String contactPersonName;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String contactPhone;
    
    @Email(message = "Invalid email format")
    private String contactEmail;

    @Pattern(regexp = "^(|[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1})$", 
             message = "Invalid GST number format")
    private String gstNumber;

}
