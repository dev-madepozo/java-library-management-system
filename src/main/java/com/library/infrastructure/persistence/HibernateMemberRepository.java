package com.library.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import com.library.domain.entity.Member;
import com.library.repository.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class HibernateMemberRepository implements MemberRepository {

    private final EntityManagerFactory entityManagerFactory;

    public HibernateMemberRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Member save(Member member) {
        EntityManager entityManager = getEntityManager();
        boolean isTransactionOwner = entityManager != JpaEntityManagerContext.get();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            if (isTransactionOwner) {
                transaction.begin();
            }

            Member savedMember;

            if (member.getId() == null) {
                entityManager.persist(member);
                savedMember = member;
            } else {
                savedMember = entityManager.merge(member);
            }

            if (isTransactionOwner) {
                transaction.commit();
            }

            return savedMember;
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
    public Optional<Member> findById(Long id) {
        EntityManager entityManager = getEntityManager();

        try {
            return Optional.ofNullable(entityManager.find(Member.class, id));
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        EntityManager entityManager = getEntityManager();

        try {
            return entityManager.createQuery(
                    "SELECT m FROM Member m WHERE m.email = :email",
                    Member.class).setParameter("email", email)
                    .getResultStream()
                    .findFirst();
        } finally {
            closeEntityManagerIfNecessary(entityManager);
        }
    }

    @Override
    public List<Member> findAll() {
        EntityManager entityManager = getEntityManager();

        try {
            return entityManager.createQuery(
                    "SELECT m FROM Member m", Member.class).getResultList();
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

}
