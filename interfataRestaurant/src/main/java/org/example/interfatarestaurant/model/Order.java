package org.example.interfatarestaurant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;
    private double totalAmount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
        this.date = LocalDateTime.now();
    }

    public Order(User user) {
        this.date = LocalDateTime.now();
        this.user = user;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public double getTotalAmount() { return totalAmount; }
    public List<OrderItem> getItems() { return items; }
    public User getUser() { return user; }
    public LocalDateTime getDate() { return date; }
    public Long getId() { return id; }
}