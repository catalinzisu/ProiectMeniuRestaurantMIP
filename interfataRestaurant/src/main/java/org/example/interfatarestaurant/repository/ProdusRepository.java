package org.example.interfatarestaurant.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.example.interfatarestaurant.model.Produs;
import java.io.File;
import java.util.Arrays;
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

    /**
     * Găsește un produs după ID
     */
    public Produs findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produs.class, id);
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
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Actualizează un produs existent
     */
    public boolean updateProdus(Produs p) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(p);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Șterge un produs după ID
     */
    public boolean deleteProdus(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Produs produs = em.find(Produs.class, id);
            if (produs != null) {
                em.remove(produs);
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

    /**
     * Exportă produsele într-un fișier JSON
     */
    public boolean exportToJson(List<Produs> products, String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(filePath), products);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Importă produse dintr-un fișier JSON
     */
    public List<Produs> importFromJson(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator());
            Produs[] products = mapper.readValue(new File(filePath), Produs[].class);
            return Arrays.asList(products);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}