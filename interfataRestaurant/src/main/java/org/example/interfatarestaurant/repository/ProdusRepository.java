package org.example.interfatarestaurant.repository;

import jakarta.persistence.EntityManager;
import org.example.interfatarestaurant.model.Produs;
import java.util.List;

public class ProdusRepository {
    private EntityManager getEntityManager() {
        return PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    public List<Produs> gasesteToate() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produs p", Produs.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void adaugaProdus(Produs p) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}