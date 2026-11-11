package com.library.service;

import java.util.List;
import java.util.Optional;

import com.library.domain.entity.Author;
import com.library.repository.AuthorRepository;

public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author createAuthor(String name, String nationality) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Author name is required.");
        }

        String normalizedName = name.trim();
        String normalizedNationality = nationality.trim();
        Author author = new Author(normalizedName, normalizedNationality);
        return this.authorRepository.save(author);
    }

    public Optional<Author> getAuthorById(Long id) {
        return this.authorRepository.findById(id);
    }

    public List<Author> getAllAuthors() {
        return this.authorRepository.findAll();
    }
}
