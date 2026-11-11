package com.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.library.domain.entity.Author;
import com.library.domain.entity.Book;
import com.library.domain.exception.AuthorNotFoundException;
import com.library.domain.exception.BookAlreadyExistsException;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;

public class BookServiceTest {

    private BookService bookService;
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private Author author;

    @BeforeEach
    void setUp() {
        // ARRANGE: Prepare common test dependencies and data.
        bookRepository = Mockito.mock(BookRepository.class);
        authorRepository = Mockito.mock(AuthorRepository.class);
        bookService = new BookService(bookRepository, authorRepository);

        author = new Author(1L, "Douglas Crockford", "USA");

        // Configure mock behavior.
        when(bookRepository.findByIsbn("978-0596517748")).thenReturn(Optional.empty());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
    }

    @Test
    void shouldCreateBookSuccessfully() {
        // ACT: Execute the method being tested.
        Book createdBook = bookService.createBook(
                "Javascript: The Good Parts",
                "978-0596517748",
                2008,
                author);

        // ASSERT: Verify the returned result.
        assertNotNull(createdBook);
        assertEquals("Javascript: The Good Parts", createdBook.getTitle());
        assertEquals("978-0596517748", createdBook.getIsbn());

        // ASSERT: Verify the expected interaction with the mock.
        verify(bookRepository, Mockito.times(1)).save(createdBook);
    }

    @Test
    void shouldThrowExceptionWhenBookAlreadyExists() {
        // ARRANGE: Prepare the scenario where the book already exists.
        Book existingBook = new Book("Javascript: The Good Parts", "978-0596517748", 2008, author);
        when(bookRepository.findByIsbn("978-0596517748")).thenReturn(Optional.of(existingBook));

        // ACT + ASSERT: Execute the method and verify that it throws an exception.
        assertThrows(BookAlreadyExistsException.class,
                () -> bookService.createBook("Javascript: The Good Parts", "978-0596517748", 2008, author));

        verify(bookRepository, Mockito.never()).save(Mockito.any(Book.class));
    }

    @Test
    void shouldThrowExceptionWhenAuthorNotFound() {
        // ARRANGE: Author is not found
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(AuthorNotFoundException.class,
                () -> bookService.createBook("Javascript: The Good Parts", "978-0596517748", 2008, author));

        // ASSERT: Verify that no book was saved.
        verify(bookRepository, Mockito.never()).save(Mockito.any(Book.class));

    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
        // ARRANGE: No additional setup required.

        // ACT + ASSERT
        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook("   ", "978-0596517748", 2008, author));

        // ASSERT: Verify that no book was saved.
        verify(bookRepository, Mockito.never()).save(Mockito.any(Book.class));
    }

    @Test
    void shouldThrowExceptionWhenIsbnIsBlank() {
        // ARRANGE: No additional setup required.

        // ACT + ASSERT
        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook("Javascript: The Good Parts", "   ", 2008, author));

        // ASSERT: Verify that no book was saved.
        verify(bookRepository, Mockito.never()).save(Mockito.any(Book.class));
    }

    @Test
    void shouldThrowExceptionWhenPublicationYearIsInvalid() {
        // ARRANGE: No additional setup required.

        // ACT + ASSERT
        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook("Javascript: The Good Parts", "978-0596517748", 0, author));

        // ASSERT: Verify that no book was saved.
        verify(bookRepository, Mockito.never()).save(Mockito.any(Book.class));
    }

    @Test
    void shouldThrowExceptionWhenPublicationYearIsInTheFuture() {
        // ARRANGE: No additional setup required.

        // ACT + ASSERT
        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook("Javascript: The Good Parts", "978-0596517748",
                        LocalDate.now().getYear() + 1, author));

        // ASSERT: Verify that no book was saved.
        verify(bookRepository, Mockito.never()).save(Mockito.any(Book.class));
    }

    @Test
    void shouldReturnBookWhenIdExists() {
        // ARRANGE:
        Book book = new Book("Javascript: The Good Parts", "978-0596517748", 2008, author);
        when(bookRepository.findById(1L)).thenReturn((Optional.of(book)));

        // ACT + ASSERT
        Optional<Book> foundBook = bookService.getBookById(1L);

        assertTrue(foundBook.isPresent());
        assertEquals(book, foundBook.get());
    }
}
