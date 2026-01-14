package com.KC.Enterprises.controller;

import com.KC.Enterprises.dto.ApiResponse;
import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.TeacherRequest;
import com.KC.Enterprises.dto.TeacherResponse;
import com.KC.Enterprises.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "Teacher management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class TeacherController {
    
    private final TeacherService teacherService;
    
    @PostMapping
    @Operation(summary = "Create a new teacher")
    public ResponseEntity<ApiResponse<TeacherResponse>> createTeacher(
            @Valid @RequestBody TeacherRequest request) {
        TeacherResponse response = teacherService.createTeacher(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Teacher created successfully"));
    }
    
    @GetMapping
    @Operation(summary = "Get all teachers")
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> getAllTeachers() {
        List<TeacherResponse> teachers = teacherService.getAllTeachers();
        return ResponseEntity.ok(ApiResponse.success(teachers, "Teachers retrieved successfully"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get teacher by ID")
    public ResponseEntity<ApiResponse<TeacherResponse>> getTeacherById(@PathVariable Long id) {
        TeacherResponse teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(ApiResponse.success(teacher, "Teacher retrieved successfully"));
    }
    
    @GetMapping("/school/{schoolId}")
    @Operation(summary = "Get teachers by school ID")
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> getTeachersBySchoolId(
            @PathVariable Long schoolId) {
        List<TeacherResponse> teachers = teacherService.getTeachersBySchoolId(schoolId);
        return ResponseEntity.ok(ApiResponse.success(teachers, "Teachers retrieved successfully"));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search teachers")
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> searchTeachers(
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) String teacherName,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean isHeadTeacher) {
        
        List<TeacherResponse> results = teacherService.searchTeachers(schoolId, teacherName, mobile, email, isHeadTeacher);
        return ResponseEntity.ok(ApiResponse.success(results, "Search completed successfully"));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update teacher")
    public ResponseEntity<ApiResponse<TeacherResponse>> updateTeacher(
            @PathVariable Long id,
            @Valid @RequestBody TeacherRequest request) {
        TeacherResponse response = teacherService.updateTeacher(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Teacher updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete teacher")
    public ResponseEntity<ApiResponse<DeleteResponse>> deleteTeacher(@PathVariable Long id) {
        DeleteResponse response = teacherService.deleteTeacher(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Teacher deleted successfully"));
    }
}