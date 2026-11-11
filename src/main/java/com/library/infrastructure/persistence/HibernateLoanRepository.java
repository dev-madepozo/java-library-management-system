package com.library.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.library.domain.entity.Loan;
import com.library.domain.enums.LoanStatus;
import com.library.repository.LoanRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class HibernateLoanRepository implements LoanRepository {

    private final EntityManagerFactory entityManagerFactory;

    public HibernateLoanRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Loan save(Loan loan) {
        EntityManager entityManager = getEntityManager();
        boolean isTransactionOwner = entityManager != JpaEntityManagerContext.get();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            if (isTransactionOwner) {
                transaction.begin();
            }

            Loan savedLoan;

            if (loan.getId() == null) {
                entityManager.persist(loan);
                savedLoan = loan;
            } else {
                savedLoan = entityManager.merge(loan);
            }

            if (isTransactionOwner) {
                transaction.commit();
            }

            return savedLoan;
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
    public Optional<Loan> findById(Long loanId) {
        EntityManager entityManager = getEntityManager();

        try {
            return Optional.ofNullable(entityManager.find(Loan.class, loanId));
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public Optional<Loan> findByIdWithDetails(Long loanId) {
        EntityManager entityManager = getEntityManager();

        try {
            String jpql = """
                        SELECT l FROM Loan l
                        JOIN FETCH l.book
                        JOIN FETCH l.member
                        WHERE l.id = :id
                    """;

            return entityManager.createQuery(jpql, Loan.class)
                    .setParameter("id", loanId).getResultStream().findFirst();
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public List<Loan> findAll() {
        EntityManager entityManager = getEntityManager();

        try {
            return entityManager.createQuery(
                    "SELECT l FROM Loan l",
                    Loan.class).getResultList();
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public List<Loan> findByMemberId(Long memberId) {
        EntityManager entityManager = getEntityManager();

        try {
            String jpql = """
                        SELECT l
                        FROM Loan l
                        JOIN FETCH l.book
                        WHERE l.member.id = :memberId
                    """;
            return entityManager.createQuery(
                    jpql,
                    Loan.class).setParameter("memberId", memberId).getResultList();
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public boolean existsUnreturnedLoanByBookId(Long bookId) {
        EntityManager entityManager = getEntityManager();

        try {
            String jpql = """
                        SELECT COUNT(l)
                        FROM Loan l
                        WHERE l.book.id = :bookId
                        AND l.status IN :statuses
                    """;
            return entityManager.createQuery(jpql, Long.class)
                    .setParameter("bookId", bookId)
                    .setParameter("statuses", List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE))
                    .getSingleResult() > 0L;
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public int updateOverdueLoans(LocalDate currentDate) {
        EntityManager entityManager = getEntityManager();
        boolean isTransactionOwner = entityManager != JpaEntityManagerContext.get();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            if (isTransactionOwner) {
                transaction.begin();
            }

            String jpql = """
                        UPDATE Loan l
                        SET l.status = :newStatus
                        WHERE l.status = :currentStatus
                        AND l.dueDate < :currentDate
                    """;
            int updatedRows = entityManager.createQuery(jpql)
                    .setParameter("newStatus", LoanStatus.OVERDUE)
                    .setParameter("currentStatus", LoanStatus.ACTIVE)
                    .setParameter("currentDate", currentDate)
                    .executeUpdate();
            entityManager.clear();
            if (isTransactionOwner) {
                transaction.commit();
            }

            return updatedRows;
        } catch (RuntimeException e) {
            if (isTransactionOwner && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    private EntityManager getEntityManager() {
        EntityManager current = JpaEntityManagerContext.get();

        return current != null ? current : this.entityManagerFactory.createEntityManager();
    }

    private void closeEntityManagerIfNecessary(EntityManager entityManager) {
        if (entityManager != JpaEntityManagerContext.get()) {
            entityManager.close();
        }
    }
}
