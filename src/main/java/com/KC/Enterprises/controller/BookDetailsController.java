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
import com.KC.Enterprises.dto.BookDetailsRequest;
import com.KC.Enterprises.dto.BookDetailsResponse;
import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.service.BookDetailsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/book-details")
@RequiredArgsConstructor
@Tag(name = "Book Details", description = "Book Details management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class BookDetailsController {

    private final BookDetailsService bookDetailsService;

    @PostMapping
    @Operation(summary = "Create new book details")
    public ResponseEntity<ApiResponse<BookDetailsResponse>> createBookDetails(
            @Valid @RequestBody BookDetailsRequest request) {
        BookDetailsResponse response = bookDetailsService.createBookDetails(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Book details created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all book details")
    public ResponseEntity<ApiResponse<List<BookDetailsResponse>>> getAllBookDetails() {
        List<BookDetailsResponse> bookDetails = bookDetailsService.getAllBookDetails();
        return ResponseEntity.ok(ApiResponse.success(bookDetails, "Book details retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book details by ID")
    public ResponseEntity<ApiResponse<BookDetailsResponse>> getBookDetailsById(@PathVariable Long id) {
        BookDetailsResponse response = bookDetailsService.getBookDetailsById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Book details retrieved successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search book details")
    public ResponseEntity<ApiResponse<List<BookDetailsResponse>>> searchBookDetails(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String classLevel,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String board) {
        
        List<BookDetailsResponse> results = bookDetailsService.searchBookDetails(title, classLevel, subject, board);
        return ResponseEntity.ok(ApiResponse.success(results, "Search completed successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update book details")
    public ResponseEntity<ApiResponse<BookDetailsResponse>> updateBookDetails(
            @PathVariable Long id,
            @Valid @RequestBody BookDetailsRequest request) {
        BookDetailsResponse response = bookDetailsService.updateBookDetails(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Book details updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book details")
    public ResponseEntity<ApiResponse<DeleteResponse>> deleteBookDetails(@PathVariable Long id) {
        DeleteResponse response = bookDetailsService.deleteBookDetails(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Book details deleted successfully"));
    }
}