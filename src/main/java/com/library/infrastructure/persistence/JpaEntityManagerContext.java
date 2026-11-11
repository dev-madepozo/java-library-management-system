package com.library.infrastructure.persistence;

import jakarta.persistence.EntityManager;

public class JpaEntityManagerContext {

    private static final ThreadLocal<EntityManager> CURRENT = new ThreadLocal<>();

    public static void set(EntityManager entityManager) {
        CURRENT.set(entityManager);
    }

    public static EntityManager get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
