package com.KC.Enterprises.service;

import java.util.List;

import com.KC.Enterprises.dto.BookDetailsRequest;
import com.KC.Enterprises.dto.BookDetailsResponse;
import com.KC.Enterprises.dto.DeleteResponse;

public interface BookDetailsService {

    BookDetailsResponse createBookDetails(BookDetailsRequest request);
    BookDetailsResponse getBookDetailsById(Long id);
    List<BookDetailsResponse> getAllBookDetails();
    List<BookDetailsResponse> searchBookDetails(String title, String classLevel, String subject, String board);
    BookDetailsResponse updateBookDetails(Long id,BookDetailsRequest request);
    DeleteResponse deleteBookDetails(Long id); 
}
