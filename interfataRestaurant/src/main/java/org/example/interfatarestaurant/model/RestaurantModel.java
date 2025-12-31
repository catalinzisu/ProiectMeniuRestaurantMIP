package org.example.interfatarestaurant.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.interfatarestaurant.RestaurantManager;
import org.example.interfatarestaurant.repository.PersistenceManager;
import org.example.interfatarestaurant.repository.ProdusRepository;
import org.example.interfatarestaurant.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class RestaurantModel {
    private User currentUser;
    private RestaurantTable currentTable;
    private final ObservableList<OrderItem> cartItems = FXCollections.observableArrayList();
    private final UserRepository userRepo = new UserRepository();
    private final ProdusRepository prodRepo = new ProdusRepository();

    private boolean happyHourActive = false;
    private boolean mealDealActive = false;
    private boolean partyPackActive = false;

    public RestaurantModel() {
        if (userRepo.findByUsernameAndPassword("admin", "admin") == null) {
            userRepo.adaugaUser(new User("admin", "admin", Role.MANAGER));
            userRepo.adaugaUser(new User("osp", "123", Role.WAITER));
        }
        ensureTablesExist();
    }

    public void resetAllTables() {
        EntityManager em = getEm();
        em.getTransaction().begin();
        List<RestaurantTable> tables = em.createQuery("SELECT t FROM RestaurantTable t", RestaurantTable.class).getResultList();
        for (RestaurantTable t : tables) {
            t.setOccupied(false);
        }
        em.getTransaction().commit();
        em.close();
    }

    public List<Order> getOrderHistory(User waiter) {
        EntityManager em = getEm();
        List<Order> orders = em.createQuery("SELECT o FROM Order o WHERE o.user.id = :uid ORDER BY o.id DESC", Order.class)
                .setParameter("uid", waiter.getId())
                .getResultList();
        em.close();
        return orders;
    }

    public List<Order> getAllOrders() {
        EntityManager em = getEm();
        List<Order> orders = em.createQuery("SELECT o FROM Order o ORDER BY o.id DESC", Order.class)
                .getResultList();
        em.close();
        return orders;
    }

    public boolean login(String user, String pass) {
        User u = userRepo.findByUsernameAndPassword(user, pass);
        if (u != null) {
            this.currentUser = u;
            return true;
        }
        return false;
    }

    public void logout() {
        this.currentUser = null;
        this.currentTable = null;
        this.cartItems.clear();
    }

    public User getCurrentUser() { return currentUser; }

    public List<RestaurantTable> getAllTables() {
        EntityManager em = getEm();
        List<RestaurantTable> tables = em.createQuery("SELECT t FROM RestaurantTable t ORDER BY t.name", RestaurantTable.class).getResultList();
        em.close();
        return tables;
    }

    public void occupyTable(RestaurantTable t) {
        this.currentTable = t;
        updateTableStatus(t.getId(), true);
    }

    public void freeTable() {
        if (currentTable != null) {
            updateTableStatus(currentTable.getId(), false);
            currentTable = null;
        }
    }

    private void updateTableStatus(Long id, boolean status) {
        EntityManager em = getEm();
        em.getTransaction().begin();
        RestaurantTable t = em.find(RestaurantTable.class, id);
        if (t != null) t.setOccupied(status);
        em.getTransaction().commit();
        em.close();
    }

    private void ensureTablesExist() {
        if (getAllTables().isEmpty()) {
            EntityManager em = getEm();
            em.getTransaction().begin();
            for (int i = 1; i <= 6; i++) em.persist(new RestaurantTable("Masa " + i));
            em.getTransaction().commit();
            em.close();
        }
    }

    public List<Produs> getAllProducts() { return prodRepo.gasesteToate(); }
    public ObservableList<OrderItem> getCart() { return cartItems; }

    public void addToCart(Produs p) {
        for (OrderItem item : cartItems) {
            if (item.getProdus().getId().equals(p.getId()) && item.getProdus().getPret() > 0) {
                item.setQuantity(item.getQuantity() + 1);
                cartItems.set(cartItems.indexOf(item), item);
                recalculateOffers();
                return;
            }
        }
        cartItems.add(new OrderItem(p, 1));
        recalculateOffers();
    }

    public void clearCart() { cartItems.clear(); }

    public void setHappyHour(boolean v) { happyHourActive = v; recalculateOffers(); }
    public void setMealDeal(boolean v) { mealDealActive = v; recalculateOffers(); }
    public void setPartyPack(boolean v) { partyPackActive = v; recalculateOffers(); }

    private void recalculateOffers() {
        cartItems.removeIf(i -> i.getProdus().getPret() < 0);
        List<OrderItem> discounts = new ArrayList<>();

        if (happyHourActive) {
            long drinkCount = cartItems.stream()
                    .filter(i -> i.getProdus() instanceof Bautura && i.getProdus().getPret() > 0)
                    .mapToInt(OrderItem::getQuantity).sum();
            int discountCount = (int) drinkCount / 2;
            if (discountCount > 0) {
                Produs p = new Bautura("OFERTA: Happy Hour", -10.0 * discountCount, Categorie.BauturaRacoritoare, 0, false);
                discounts.add(new OrderItem(p, 1));
            }
        }
        cartItems.addAll(discounts);
    }

    public double calculateTotal() {
        return cartItems.stream().mapToDouble(i -> i.getProdus().getPret() * i.getQuantity()).sum();
    }

    public void placeOrder() {
        if (cartItems.isEmpty() || currentTable == null) return;
        EntityManager em = getEm();
        em.getTransaction().begin();

        // --- AICI AM REPARAT CONSTRUCTORUL ---
        // Acum trimitem si numele mesei catre Order
        Order order = new Order(currentUser, currentTable.getName());

        order.setTotalAmount(calculateTotal());
        for (OrderItem oi : cartItems) {
            if (oi.getProdus().getPret() > 0) {
                Produs dbProd = em.find(Produs.class, oi.getProdus().getId());
                if(dbProd != null) order.addItem(new OrderItem(dbProd, oi.getQuantity()));
            }
        }
        em.persist(order);
        em.getTransaction().commit();
        em.close();
        clearCart();
    }

    public void addProduct(Produs p) { prodRepo.adaugaProdus(p); }
    public void addUser(User u) { userRepo.adaugaUser(u); }
    public void deleteUser(User u) {
        EntityManager em = getEm();
        em.getTransaction().begin();
        User found = em.find(User.class, u.getId());
        if(found != null) em.remove(found);
        em.getTransaction().commit();
        em.close();
    }
    public List<User> getWaiters() {
        EntityManager em = getEm();
        List<User> list = em.createQuery("SELECT u FROM User u WHERE u.role = :r", User.class)
                .setParameter("r", Role.WAITER).getResultList();
        em.close();
        return list;
    }

    public void importaDate() {
        try {
            List<Produs> produseNoi = RestaurantManager.importaMeniu("meniu_export.json");
            for (Produs p : produseNoi) prodRepo.adaugaProdus(p);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void exportaDate() throws Exception {
        RestaurantManager.exportaMeniu(getAllProducts(), "meniu_export.json");
    }

    private EntityManager getEm() { return PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager(); }
}