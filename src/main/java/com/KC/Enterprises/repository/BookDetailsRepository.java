package com.KC.Enterprises.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.KC.Enterprises.entity.BookDetails;

public interface BookDetailsRepository extends JpaRepository<BookDetails, Long> {
    List<BookDetails> findByTitle(String title);
    
    // Search by class level
    List<BookDetails> findByClassLevel(String classLevel);
    
    // Search by subject
    List<BookDetails> findBySubject(String subject);
    
    // Search by board
    List<BookDetails> findByBoard(String board);

    // Check if ISBN exists (for validation)
    boolean existsByIsbn(String isbn);
    
    // Find by ISBN
    BookDetails findByIsbn(String isbn);

    @Query("SELECT b FROM BookDetails b WHERE " +
       "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', CAST(:title AS text), '%'))) AND " +
       "(:classLevel IS NULL OR LOWER(b.classLevel) LIKE LOWER(CONCAT('%', CAST(:classLevel AS text), '%'))) AND " +
       "(:subject IS NULL OR LOWER(b.subject) LIKE LOWER(CONCAT('%', CAST(:subject AS text), '%'))) AND " +
       "(:board IS NULL OR LOWER(b.board) LIKE LOWER(CONCAT('%', CAST(:board AS text), '%')))")
List<BookDetails> searchBookDetails(
        @Param("title") String title,
        @Param("classLevel") String classLevel,
        @Param("subject") String subject,
        @Param("board") String board);
    }
