package org.example.interfatarestaurant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double totalAmount;
    private LocalDateTime orderDate;
    private String tableName; // Adaugam numele mesei pentru istoric

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    public Order() {}

    public Order(User user, String tableName) {
        this.user = user;
        this.tableName = tableName;
        this.orderDate = LocalDateTime.now();
    }

    public void addItem(OrderItem item) { items.add(item); }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public Long getId() { return id; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getTableName() { return tableName; }
    public User getUser() { return user; }
    public List<OrderItem> getItems() { return items; }

    // TOSTRING PENTRU AFISARE FRUMOASA IN LISTA (In caz ca nu folosim tabel)
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return String.format("Masa: %s | Data: %s | Total: %.2f RON | Ospatar: %s",
                tableName != null ? tableName : "N/A",
                orderDate.format(formatter),
                totalAmount,
                user.getUsername());
    }
}