package org.example.interfatarestaurant.service;

import org.example.interfatarestaurant.model.*;
import org.example.interfatarestaurant.repository.PersistenceManager;
import org.example.interfatarestaurant.repository.ProdusRepository;
import org.example.interfatarestaurant.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Service layer - gestionează logica de business
 * Separat de repository și controller
 */
public class RestaurantService {
    private final UserRepository userRepository = new UserRepository();
    private final ProdusRepository produsRepository = new ProdusRepository();

    // ============ GESTIONARE ANGAJAȚI (STAFF) ============

    /**
     * Obține toți angajații (utilizatori cu rol WAITER)
     */
    public List<User> getAllStaff() {
        return userRepository.findAllByRole(Role.WAITER);
    }

    /**
     * Adaugă un nou angajat
     */
    public boolean addStaff(String username, String password) {
        try {
            // Verifică dacă utilizatorul nu există deja
            if (userRepository.findByUsername(username) != null) {
                return false; // Utilizator existent
            }
            User newStaff = new User(username, password, Role.WAITER);
            userRepository.adaugaUser(newStaff);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Șterge un angajat și toate comenzile asociate (cascadă)
     */
    public boolean deleteStaff(User staff) {
        try {
            return userRepository.deleteUser(staff.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============ GESTIONARE MENIU (PRODUS) ============

    /**
     * Obține toate produsele din baza de date
     */
    public List<Produs> getAllProducts() {
        return produsRepository.gasesteToate();
    }

    /**
     * Adaugă un produs nou (Mancare sau Bautura)
     */
    public boolean addProduct(String tip, String nume, double pret, int cantitate,
                               String descriere, boolean isSpecial) {
        try {
            Produs produs;
            if ("Mancare".equals(tip)) {
                Mancare mancare = new Mancare(nume, pret, Categorie.FelPrincipal, cantitate, isSpecial);
                mancare.setDescriere(descriere);
                produs = mancare;
            } else if ("Bautura".equals(tip)) {
                Bautura bautura = new Bautura(nume, pret, Categorie.BauturaRacoritoare, cantitate, isSpecial);
                bautura.setDescriere(descriere);
                produs = bautura;
            } else {
                return false;
            }

            produsRepository.adaugaProdus(produs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualizează un produs existent
     */
    public boolean updateProduct(Long id, String tip, String nume, double pret,
                                 int cantitate, String descriere, boolean isSpecial) {
        try {
            Produs produs = produsRepository.findById(id);
            if (produs == null) return false;

            produs.setNume(nume);
            produs.setPret(pret);
            produs.setDescriere(descriere);

            if (produs instanceof Mancare) {
                Mancare m = (Mancare) produs;
                m.setGramaj(cantitate);
                m.setVegetarian(isSpecial);
            } else if (produs instanceof Bautura) {
                Bautura b = (Bautura) produs;
                b.setVolum(cantitate);
                b.setAlcoholic(isSpecial);
            }

            produsRepository.updateProdus(produs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Șterge un produs
     */
    public boolean deleteProduct(Long id) {
        try {
            return produsRepository.deleteProdus(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============ IMPORT/EXPORT JSON ============

    /**
     * Exportă toate produsele într-un fișier JSON
     */
    public boolean exportProductsToJson(String filePath) {
        try {
            List<Produs> products = getAllProducts();
            return produsRepository.exportToJson(products, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Importă produse dintr-un fișier JSON
     */
    public boolean importProductsFromJson(String filePath) {
        try {
            List<Produs> products = produsRepository.importFromJson(filePath);
            for (Produs p : products) {
                produsRepository.adaugaProdus(p);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============ GESTIONARE OFERTE ============

    /**
     * Obține starea curentă a ofertelor
     */
    public OfferState getOfferState() {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        try {
            OfferState state = em.find(OfferState.class, 1L);
            if (state == null) {
                // Creează o nouă stare cu valorile implicite
                state = new OfferState();
                em.getTransaction().begin();
                em.persist(state);
                em.getTransaction().commit();
            }
            return state;
        } finally {
            em.close();
        }
    }

    /**
     * Actualizează starea ofertelor
     */
    public boolean updateOfferState(boolean happyHour, boolean mealDeal, boolean partyPack) {
        try {
            EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();

            OfferState state = em.find(OfferState.class, 1L);
            if (state == null) {
                state = new OfferState();
            }

            state.setHappyHourActive(happyHour);
            state.setMealDealActive(mealDeal);
            state.setPartyPackActive(partyPack);

            em.merge(state);
            em.getTransaction().commit();
            em.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============ GESTIONARE MESE ============

    /**
     * Resetează toate mesele
     */
    public boolean resetAllTables() {
        try {
            EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();
            List<RestaurantTable> tables = em.createQuery("SELECT t FROM RestaurantTable t", RestaurantTable.class)
                    .getResultList();
            for (RestaurantTable t : tables) {
                t.setOccupied(false);
            }
            em.getTransaction().commit();
            em.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

