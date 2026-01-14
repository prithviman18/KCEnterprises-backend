package com.KC.Enterprises.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/test")
@Tag(name = "Test", description = "Test API")
public class TestController {

    @GetMapping("/hello")
    @Operation(summary = "Test endpoint")
    public String hello() {
        return "Hello from KC Enterprises!";
    }
}
