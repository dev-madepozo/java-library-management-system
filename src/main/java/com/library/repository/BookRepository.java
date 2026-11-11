package com.library.repository;

import java.util.List;
import java.util.Optional;

import com.library.domain.entity.Book;
import com.library.dto.BookSummaryDTO;

public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(Long id);

    Optional<Book> findByIdWithAuthor(Long id);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findAll();

    List<String> findAllTitles();

    List<BookSummaryDTO> findAllBookSummaries();

    Optional<Book> findByIdForUpdate(Long id);

    List<Book> findAvailableBooks();
}
