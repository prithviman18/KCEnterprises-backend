package com.KC.Enterprises.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.KC.Enterprises.dto.ApiResponse;
import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.SchoolRequest;
import com.KC.Enterprises.dto.SchoolResponse;
import com.KC.Enterprises.service.SchoolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
@Tag(name = "Schools", description = "School management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class SchoolController {

    private final SchoolService schoolService;

    @PostMapping
    @Operation(summary = "Create a new school")
    public ResponseEntity<ApiResponse<SchoolResponse>> createSchool(
            @Valid @RequestBody SchoolRequest request) {
        SchoolResponse response = schoolService.createSchool(request);
        return ResponseEntity.ok(ApiResponse.success(response, "School created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all schools")
    public ResponseEntity<ApiResponse<List<SchoolResponse>>> getAllSchools() {
        List<SchoolResponse> schools = schoolService.getAllSchools();
        return ResponseEntity.ok(ApiResponse.success(schools, "Schools retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get school by ID")
    public ResponseEntity<ApiResponse<SchoolResponse>> getSchoolById(@PathVariable Long id) {
        SchoolResponse school = schoolService.getSchoolById(id);
        return ResponseEntity.ok(ApiResponse.success(school, "School retrieved successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search schools")
    public ResponseEntity<ApiResponse<List<SchoolResponse>>> searchSchools(
            @RequestParam(required = false) String schoolName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String contactPerson) {
        
        List<SchoolResponse> results = schoolService.searchSchools(schoolName, city, contactPerson);
        return ResponseEntity.ok(ApiResponse.success(results, "Search completed successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update school")
    public ResponseEntity<ApiResponse<SchoolResponse>> updateSchool(
            @PathVariable Long id,
            @Valid @RequestBody SchoolRequest request) {
        SchoolResponse response = schoolService.updateSchool(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "School updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete school")
    public ResponseEntity<ApiResponse<DeleteResponse>> deleteSchool(@PathVariable Long id) {
        DeleteResponse response = schoolService.deleteSchool(id);
        return ResponseEntity.ok(ApiResponse.success(response, "School deleted successfully"));
    }
}