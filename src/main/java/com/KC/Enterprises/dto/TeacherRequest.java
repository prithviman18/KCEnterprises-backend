package com.KC.Enterprises.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRequest {
    
    @NotBlank(message = "Teacher name is required")
    private String teacherName;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobile;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    
    @NotNull(message = "School Id is required")
    private Long schoolId;
    
    private String designation;
    
    private Boolean isHeadTeacher;
}