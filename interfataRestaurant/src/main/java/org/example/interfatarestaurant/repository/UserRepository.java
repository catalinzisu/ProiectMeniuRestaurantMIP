package org.example.interfatarestaurant.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.example.interfatarestaurant.model.User;

public class UserRepository {
    private EntityManager getEntityManager() {
        return PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    public User findByUsernameAndPassword(String username, String password) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :u AND u.password = :p", User.class)
                    .setParameter("u", username)
                    .setParameter("p", password)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void adaugaUser(User u) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(u);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}