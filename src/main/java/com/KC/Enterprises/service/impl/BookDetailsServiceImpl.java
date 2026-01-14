package com.KC.Enterprises.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.KC.Enterprises.dto.BookDetailsRequest;
import com.KC.Enterprises.dto.BookDetailsResponse;
import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.entity.BookDetails;
import com.KC.Enterprises.exception.ResourceNotFoundException;
import com.KC.Enterprises.repository.BookDetailsRepository;
import com.KC.Enterprises.service.BookDetailsService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class BookDetailsServiceImpl implements BookDetailsService {
    
    private final BookDetailsRepository bookDetailsRepository;

    @Override
    public BookDetailsResponse createBookDetails(BookDetailsRequest request){
        BookDetails bookDetails =  BookDetails.builder()
            .title(request.getTitle())
            .classLevel(request.getClassLevel())
            .subject(request.getSubject())
            .board(request.getBoard())
            .edition(request.getEdition())
            .isbn(request.getIsbn())
            .publisher(request.getPublisher())
            .build();

        BookDetails savedBookDetails = bookDetailsRepository.save(bookDetails);
        return mapToResponse(savedBookDetails);
    }

    @Override
    public BookDetailsResponse getBookDetailsById(Long id) {
        BookDetails bookDetails = bookDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book details not found with id: " + id));
        return mapToResponse(bookDetails);
    }

    @Override
    public List<BookDetailsResponse> getAllBookDetails() {
        return bookDetailsRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDetailsResponse> searchBookDetails(String title, String classLevel, String subject, String board) {
    
        return bookDetailsRepository.searchBookDetails(title, classLevel, subject, board)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookDetailsResponse updateBookDetails(Long id, BookDetailsRequest request) {
        BookDetails bookDetails = bookDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book details not found with id: " + id));

        bookDetails.setTitle(request.getTitle());
        bookDetails.setClassLevel(request.getClassLevel());
        bookDetails.setSubject(request.getSubject());
        bookDetails.setBoard(request.getBoard());
        bookDetails.setEdition(request.getEdition());
        bookDetails.setIsbn(request.getIsbn());
        bookDetails.setPublisher(request.getPublisher());

        BookDetails updatedBookDetails = bookDetailsRepository.save(bookDetails);
        return mapToResponse(updatedBookDetails);
    }

    @Override
    public DeleteResponse deleteBookDetails(Long id) {
        if (!bookDetailsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book details not found with id: " + id);
        }
        bookDetailsRepository.deleteById(id);
        return DeleteResponse.builder()
                .success(true)
                .message("Book details deleted successfully")
                .deletedId(id)
                .build();
    }

    private BookDetailsResponse mapToResponse(BookDetails bookDetails) {
        return BookDetailsResponse.builder()
                .id(bookDetails.getId())
                .title(bookDetails.getTitle())
                .classLevel(bookDetails.getClassLevel())
                .subject(bookDetails.getSubject())
                .board(bookDetails.getBoard())
                .edition(bookDetails.getEdition())
                .isbn(bookDetails.getIsbn())
                .publisher(bookDetails.getPublisher())
                .createdAt(bookDetails.getCreatedAt())
                .updatedAt(bookDetails.getUpdatedAt())
                .build();
    }
}