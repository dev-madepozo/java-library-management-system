package com.library.persistence;

import java.util.List;

import com.library.dto.BookSummaryDTO;
import com.library.infrastructure.persistence.HibernateBookRepository;
import com.library.repository.BookRepository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class LazyLoadingTest {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("library-persistence-unit");

        try {
            BookRepository bookRepository = new HibernateBookRepository(emf);

            // Book book = bookRepository.findByIdWithAuthor(1L).orElseThrow();
            // System.out.println("Book: " + book.getTitle());
            // System.out.println("Author: " + book.getAuthor().getName());

            // List<String> bookTitles = bookRepository.findAllTitles();

            // for (String title : bookTitles) {
            // System.out.println(title);
            // }

            List<BookSummaryDTO> summaries = bookRepository.findAllBookSummaries();

            for (BookSummaryDTO summary : summaries) {
                System.out.println(
                        summary.getTitle() + " - " +
                                summary.getIsbn() + " - " +
                                summary.getAuthorName());
            }
        } finally {
            emf.close();
        }
    }
}
