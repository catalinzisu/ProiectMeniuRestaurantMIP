package org.example.interfatarestaurant.service;

import jakarta.persistence.EntityManager;
import org.example.interfatarestaurant.model.*;
import org.example.interfatarestaurant.repository.PersistenceManager;
import org.example.interfatarestaurant.repository.ProdusRepository;
import org.example.interfatarestaurant.repository.UserRepository;

import java.util.List;

public class RestaurantService {
    private final ProdusRepository produsRepo = new ProdusRepository();
    private final UserRepository userRepo = new UserRepository();

    // --- MESE ---
    public List<RestaurantTable> getAllTables() {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        List<RestaurantTable> tables = em.createQuery("SELECT t FROM RestaurantTable t", RestaurantTable.class).getResultList();

        // Init tables if empty
        if(tables.isEmpty()) {
            em.getTransaction().begin();
            for(int i=1; i<=6; i++) em.persist(new RestaurantTable("Masa " + i));
            em.getTransaction().commit();
            tables = em.createQuery("SELECT t FROM RestaurantTable t", RestaurantTable.class).getResultList();
        }
        em.close();
        return tables;
    }

    public void elibereazaMasa(RestaurantTable table) {
        updateTableStatus(table, false);
    }

    public void ocupaMasa(RestaurantTable table) {
        updateTableStatus(table, true);
    }

    private void updateTableStatus(RestaurantTable table, boolean status) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        RestaurantTable t = em.find(RestaurantTable.class, table.getId());
        t.setOccupied(status);
        em.getTransaction().commit();
        em.close();
    }

    // --- COMENZI ---
    public void saveOrder(Order order) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            // User-ul trebuie reatașat sesiunii curente sau găsit din nou
            if (order.getUser() != null) {
                order = new Order(em.find(User.class, order.getUser().getId()));
                // Reconstruim order-ul atașat contextului, dar simplificăm pentru demo:
                // Doar persistăm.
            }
            em.persist(order);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // --- STAFF ---
    public List<User> getAllStaff() {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.role = :r", User.class)
                .setParameter("r", Role.WAITER).getResultList();
        em.close();
        return users;
    }

    public void deleteStaff(User user) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        User u = em.find(User.class, user.getId()); // Find managed entity
        if (u != null) {
            em.remove(u); // Cascade delete will handle orders due to cascade=ALL in User entity
        }
        em.getTransaction().commit();
        em.close();
    }

    // Wrapper pentru repo-uri existente
    public void addStaff(User u) { userRepo.adaugaUser(u); }
    public List<Produs> getAllProducts() { return produsRepo.gasesteToate(); }
    public void addProduct(Produs p) { produsRepo.adaugaProdus(p); }
    public void deleteProduct(Produs p) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        Produs managed = em.find(Produs.class, p.getId());
        if(managed != null) em.remove(managed);
        em.getTransaction().commit();
        em.close();
    }
}