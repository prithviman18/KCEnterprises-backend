package com.KC.Enterprises.dto;

import com.KC.Enterprises.enums.Role;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private Role role;

    private String username;
    private String firstName;
    private String lastName;
    private String phone;
}
