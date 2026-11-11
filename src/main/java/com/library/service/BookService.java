package com.library.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.library.domain.entity.Author;
import com.library.domain.entity.Book;
import com.library.domain.exception.AuthorNotFoundException;
import com.library.domain.exception.BookAlreadyExistsException;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;

public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public Book createBook(String title, String isbn, int publishedYear, Author author) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Book title is required");
        }

        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("Book isbn is required");
        }

        if (publishedYear <= 0 || publishedYear > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Invalid Published year");
        }

        if (author == null || author.getId() == null) {
            throw new IllegalArgumentException("A registered author is required");
        }

        String normalizedTitle = title.trim();
        String normalizedIsbn = isbn.trim();

        if (this.bookRepository.findByIsbn(normalizedIsbn).isPresent()) {
            throw new BookAlreadyExistsException("Book with ISBN = " + normalizedIsbn + " already exists");
        }

        Author registeredAuthor = this.authorRepository.findById(author.getId())
                .orElseThrow(() -> new AuthorNotFoundException("Author not found: " + author.getId()));

        Book book = new Book(normalizedTitle, normalizedIsbn, publishedYear, registeredAuthor);
        this.bookRepository.save(book);

        return book;
    }

    public Optional<Book> getBookById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid id");
        }

        return this.bookRepository.findById(id);
    }

    public Optional<Book> getBookByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("Book isbn is required");
        }

        String normalizedIsbn = isbn.trim();

        return this.bookRepository.findByIsbn(normalizedIsbn);
    }

    public List<Book> getAllBooks() {
        return this.bookRepository.findAll();
    }

    public List<Book> getAvailableBooks() {
        return this.bookRepository.findAvailableBooks();
    }
}
