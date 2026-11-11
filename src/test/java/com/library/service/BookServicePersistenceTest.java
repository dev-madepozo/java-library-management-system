package com.library.service;

import com.library.domain.entity.Author;
import com.library.domain.entity.Book;
import com.library.infrastructure.persistence.HibernateAuthorRepository;
import com.library.infrastructure.persistence.HibernateBookRepository;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.service.BookService;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class BookServicePersistenceTest {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("library-persistence-unit");

        try {
            // Dependency Injection
            AuthorRepository authorRepository = new HibernateAuthorRepository(emf);
            BookRepository bookRepository = new HibernateBookRepository(emf);

            BookService bookService = new BookService(bookRepository, authorRepository);

            Book book = bookService.createBook(
                    "Clean Code",
                    "978-0132350884",
                    2008,
                    new Author(2L, "Martin Fowler", "British"));

            // Result

            System.out.println("Book created: " + book.getTitle());
            System.out.println("Book ID: " + book.getId());
            System.out.println("Author ID: " + book.getAuthor().getId());
        } finally {
            emf.close();
        }
    }
}
