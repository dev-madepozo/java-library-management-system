package com.library.persistence;

import com.library.domain.entity.Author;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class AuthorPersistenceTest {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("library-persistence-unit");

        EntityManager em = emf.createEntityManager();

        EntityTransaction transaction = em.getTransaction();

        try {
            Author author = new Author("Douglas Crockford", "USA");

            transaction.begin();
            em.persist(author);
            transaction.commit();

            System.out.println("Author saved successfully!");
            System.out.println("Generated ID: " + author.getId());
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            em.close();
            emf.close();
        }
    }
}
