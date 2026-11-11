package com.library.repository;

import java.util.List;
import java.util.Optional;

import com.library.domain.entity.Author;

public interface AuthorRepository {

    Author save(Author author);

    Optional<Author> findById(Long id);

    List<Author> findAll();
}
