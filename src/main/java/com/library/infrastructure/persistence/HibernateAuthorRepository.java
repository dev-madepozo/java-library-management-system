package com.library.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import com.library.domain.entity.Author;
import com.library.repository.AuthorRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class HibernateAuthorRepository implements AuthorRepository {
    private final EntityManagerFactory entityManagerFactory;

    public HibernateAuthorRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Author save(Author author) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Author savedAuthor;

            if (author.getId() == null) {
                entityManager.persist(author);
                savedAuthor = author;
            } else {
                savedAuthor = entityManager.merge(author);
            }

            transaction.commit();

            return savedAuthor;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Optional<Author> findById(Long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        try {
            return Optional.ofNullable(entityManager.find(Author.class, id));
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Author> findAll() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        try {
            return entityManager.createQuery("SELECT a FROM Author a", Author.class).getResultList();
        } finally {
            entityManager.close();
        }
    }
}
