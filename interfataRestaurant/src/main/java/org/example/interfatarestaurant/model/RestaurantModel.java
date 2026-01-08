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

    public RestaurantTable getCurrentTable() { return currentTable; }

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
            Long itemId = item.getProdus().getId();
            Long pId = p.getId();
            // Only compare IDs if both are not null (skip discount items which have null IDs)
            if (itemId != null && pId != null && itemId.equals(pId) && item.getProdus().getPret() > 0) {
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

        // HAPPY HOUR: 50% off every 2nd drink
        if (happyHourActive) {
            List<OrderItem> drinks = cartItems.stream()
                    .filter(i -> i.getProdus() instanceof Bautura && i.getProdus().getPret() > 0)
                    .toList();

            double totalDiscount = 0.0;
            int drinkIndex = 0;

            for (OrderItem drinkItem : drinks) {
                Bautura drink = (Bautura) drinkItem.getProdus();
                int qty = drinkItem.getQuantity();

                for (int i = 0; i < qty; i++) {
                    drinkIndex++;
                    if (drinkIndex % 2 == 0) {
                        totalDiscount += drink.getPret() * 0.5;
                    }
                }
            }

            if (totalDiscount > 0) {
                Produs discountPromo = new Bautura("OFERTA: Happy Hour (-50% 2nd drink)",
                        -totalDiscount,
                        Categorie.BauturaRacoritoare, 0, false);
                discounts.add(new OrderItem(discountPromo, 1));
            }
        }

        // MEAL DEAL: 25% off cheapest dessert when Pizza is ordered
        if (mealDealActive) {
            long pizzaCount = cartItems.stream()
                    .filter(i -> i.getProdus() instanceof Mancare &&
                            ((Mancare)i.getProdus()).getCategorie() == Categorie.Pizza &&
                            i.getProdus().getPret() > 0)
                    .count();

            if (pizzaCount > 0) {
                OrderItem cheapestDessert = cartItems.stream()
                        .filter(i -> i.getProdus() instanceof Mancare &&
                                ((Mancare)i.getProdus()).getCategorie() == Categorie.Desert &&
                                i.getProdus().getPret() > 0)
                        .min((a, b) -> Double.compare(a.getProdus().getPret(), b.getProdus().getPret()))
                        .orElse(null);

                if (cheapestDessert != null) {
                    double desertDiscount = cheapestDessert.getProdus().getPret() * 0.25;
                    Produs discountPromo = new Mancare("OFERTA: Meal Deal (-25% Desert)",
                            -desertDiscount,
                            Categorie.Desert, 0, false);
                    discounts.add(new OrderItem(discountPromo, 1));
                }
            }
        }

        // PARTY PACK: 1 free pizza (cheapest) for every 4 pizzas ordered
        if (partyPackActive) {
            List<OrderItem> pizzas = cartItems.stream()
                    .filter(i -> i.getProdus() instanceof Mancare &&
                            ((Mancare)i.getProdus()).getCategorie() == Categorie.Pizza &&
                            i.getProdus().getPret() > 0)
                    .toList();

            int pizzaCount = pizzas.stream().mapToInt(OrderItem::getQuantity).sum();
            int freePizzas = pizzaCount / 4;

            for (int i = 0; i < freePizzas; i++) {
                OrderItem cheapestPizza = pizzas.stream()
                        .min((a, b) -> Double.compare(a.getProdus().getPret(), b.getProdus().getPret()))
                        .orElse(null);

                if (cheapestPizza != null) {
                    Produs discountPromo = new Mancare("OFERTA: Party Pack (1 Pizza Gratis)",
                            -cheapestPizza.getProdus().getPret(),
                            Categorie.Pizza, 0, false);
                    discounts.add(new OrderItem(discountPromo, 1));
                }
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

    /**
     * METODA NOUA: Găsește comanda activă (OPEN) pentru o masă specifică
     * Această metodă asigură că preluăm comanda CORECTĂ pentru masa selectată
     */
    public Order findActiveOrderForTable(String tableName) {
        EntityManager em = getEm();
        try {
            List<Order> orders = em.createQuery(
                    "SELECT o FROM Order o WHERE o.tableName = :tableName ORDER BY o.id DESC",
                    Order.class)
                    .setParameter("tableName", tableName)
                    .getResultList();

            // Returnează cea mai recentă comandă (care probabil e cea activă)
            return orders.isEmpty() ? null : orders.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * METODA NOUA: Calculează totalul EXACT pe baza unei comenzi din BD
     * Recalculează pe loc, nu se bazează pe câmpuri din DB neactualizate
     */
    public double calculateOrderTotal(Order order) {
        if (order == null || order.getItems().isEmpty()) return 0.0;

        EntityManager em = getEm();
        try {
            // Preluăm comanda fresh din BD cu items încărcate
            Order freshOrder = em.find(Order.class, order.getId());
            if (freshOrder == null) return 0.0;

            double total = 0.0;
            for (OrderItem item : freshOrder.getItems()) {
                Produs produs = item.getProdus();
                if (produs != null && produs.getPret() > 0) {
                    total += produs.getPret() * item.getQuantity();
                }
            }
            return total;
        } finally {
            em.close();
        }
    }

    /**
     * METODA NOUA: Afișează bon detaliat cu produse și total
     * Folosit pentru afișarea bonului înainte de plată
     */
    public String generateReceiptText(Order order) {
        if (order == null || order.getItems().isEmpty()) {
            return "Fără itemi în comandă";
        }

        EntityManager em = getEm();
        try {
            Order freshOrder = em.find(Order.class, order.getId());
            if (freshOrder == null) return "Comandă nu mai există";

            StringBuilder receipt = new StringBuilder();
            receipt.append("=== BON DE PLATĂ ===\n");
            receipt.append("Masa: ").append(freshOrder.getTableName()).append("\n");
            receipt.append("Data: ").append(freshOrder.getOrderDate()).append("\n");
            receipt.append("Ospatar: ").append(freshOrder.getUser().getUsername()).append("\n");
            receipt.append("-------------------\n");

            double total = 0.0;
            for (OrderItem item : freshOrder.getItems()) {
                Produs produs = item.getProdus();
                if (produs != null && produs.getPret() > 0) {
                    double itemTotal = produs.getPret() * item.getQuantity();
                    receipt.append(String.format("%s x%d = %.2f RON\n",
                            produs.getNume(),
                            item.getQuantity(),
                            itemTotal));
                    total += itemTotal;
                }
            }

            receipt.append("-------------------\n");
            receipt.append(String.format("TOTAL: %.2f RON\n", total));
            receipt.append("===================");

            return receipt.toString();
        } finally {
            em.close();
        }
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
