package com.library.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import com.library.domain.entity.Book;
import com.library.domain.enums.LoanStatus;
import com.library.dto.BookSummaryDTO;
import com.library.repository.BookRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

public class HibernateBookRepository implements BookRepository {

    private final EntityManagerFactory entityManagerFactory;

    public HibernateBookRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Book save(Book book) {
        EntityManager entityManager = getEntityManager();
        boolean isTransactionOwner = entityManager != JpaEntityManagerContext.get();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            if (isTransactionOwner) {
                transaction.begin();
            }

            Book savedBook;

            if (book.getId() == null) {
                entityManager.persist(book);
                savedBook = book;
            } else {
                savedBook = entityManager.merge(book);
            }

            if (isTransactionOwner) {
                transaction.commit();
            }

            return savedBook;
        } catch (RuntimeException e) {
            if (isTransactionOwner && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        EntityManager entityManager = getEntityManager();

        try {
            return Optional.ofNullable(entityManager.find(Book.class, id));
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public Optional<Book> findByIdWithAuthor(Long id) {
        EntityManager entityManager = getEntityManager();

        try {
            return entityManager.createQuery(
                    "SELECT b FROM Book b JOIN FETCH b.author WHERE b.id = :id",
                    Book.class).setParameter("id", id)
                    .getResultStream()
                    .findFirst();
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        EntityManager entityManager = getEntityManager();

        try {
            return entityManager.createQuery(
                    "SELECT b FROM Book b WHERE b.isbn = :isbn",
                    Book.class).setParameter("isbn", isbn)
                    .getResultStream()
                    .findFirst();
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public List<Book> findAll() {
        EntityManager entityManager = getEntityManager();

        try {
            return entityManager.createQuery(
                    "SELECT b FROM Book b JOIN FETCH b.author",
                    Book.class).getResultList();
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public List<String> findAllTitles() {
        EntityManager entityManager = getEntityManager();

        try {
            return entityManager.createQuery(
                    "SELECT b.title FROM Book b",
                    String.class).getResultList();
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public List<BookSummaryDTO> findAllBookSummaries() {
        EntityManager entityManager = getEntityManager();

        try {
            return entityManager.createQuery(
                    "SELECT NEW com.library.dto.BookSummaryDTO " +
                            "(b.title, b.isbn, b.author.name) " +
                            "FROM Book b",
                    BookSummaryDTO.class).getResultList();
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    private EntityManager getEntityManager() {
        EntityManager current = JpaEntityManagerContext.get();

        return current != null ? current : entityManagerFactory.createEntityManager();
    }

    private void closeEntityManagerIfNecessary(EntityManager entityManager) {
        if (entityManager != JpaEntityManagerContext.get()) {
            entityManager.close();
        }
    }

    @Override
    public Optional<Book> findByIdForUpdate(Long id) {
        EntityManager entityManager = getEntityManager();

        try {
            return Optional.ofNullable(
                    entityManager.find(
                            Book.class,
                            id,
                            LockModeType.PESSIMISTIC_WRITE));
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public List<Book> findAvailableBooks() {
        EntityManager entityManager = getEntityManager();

        try {
            String jpql = """
                        SELECT b FROM Book b
                        WHERE NOT EXISTS (
                            SELECT l FROM Loan l
                            WHERE l.book.id = b.id
                            AND l.status IN :statuses
                        )
                    """;

            return entityManager.createQuery(jpql, Book.class)
                    .setParameter("statuses", List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE)).getResultList();
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }
}
