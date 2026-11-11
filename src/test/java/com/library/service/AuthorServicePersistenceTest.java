package com.library.service;

import com.library.domain.entity.Author;
import com.library.infrastructure.persistence.HibernateAuthorRepository;
import com.library.repository.AuthorRepository;
import com.library.service.AuthorService;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class AuthorServicePersistenceTest {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("library-persistence-unit");

        try {
            // Dependency Injection

            AuthorRepository authorRepository = new HibernateAuthorRepository(emf);
            AuthorService authorService = new AuthorService(authorRepository);

            // Create
            Author author = authorService.createAuthor("Martin Fowler", "British");

            System.out.println("Created: " + author.getName());
            System.out.println("ID: " + author.getId());

            // Find
            authorService.getAuthorById(author.getId())
                    .ifPresent(found -> System.out.println("Found: " + found.getName()));

            // List
            authorService.getAllAuthors().forEach(item -> System.out.println(item.getName()));
        } finally {
            emf.close();
        }

    }
}
