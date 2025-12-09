package org.example.interfatarestaurant.repository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class PersistenceManager {
    private static final PersistenceManager INSTANCE = new PersistenceManager();
    private final EntityManagerFactory emf;

    private PersistenceManager() {
        emf = Persistence.createEntityManagerFactory("RestaurantPU");
    }

    public static PersistenceManager getInstance() { return INSTANCE; }
    public EntityManagerFactory getEntityManagerFactory() { return emf; }
    public void close() { emf.close(); }
}