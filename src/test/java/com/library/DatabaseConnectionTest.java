package com.library;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DatabaseConnectionTest {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("library-persistence-unit");
        System.out.println("Database connection successfully!");
        emf.close();
    }
}
