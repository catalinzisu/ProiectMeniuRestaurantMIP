package org.example.interfatarestaurant.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.example.interfatarestaurant.model.Role;
import org.example.interfatarestaurant.model.User;

import java.util.List;

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

    /**
     * Găsește utilizator după username
     */
    public User findByUsername(String username) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :u", User.class)
                    .setParameter("u", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Obține toți utilizatorii cu un anumit rol
     */
    public List<User> findAllByRole(Role role) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class)
                    .setParameter("role", role)
                    .getResultList();
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

    /**
     * Șterge un utilizator și toate comenzile asociate (cascadă)
     */
    public boolean deleteUser(Long userId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userId);
            if (user != null) {
                em.remove(user);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}