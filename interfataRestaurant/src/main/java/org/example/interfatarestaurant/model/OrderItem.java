package org.example.interfatarestaurant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "produs_id")
    private Produs produs;

    private int quantity;

    public OrderItem() {}
    public OrderItem(Produs produs, int quantity) {
        this.produs = produs;
        this.quantity = quantity;
    }

    public void setOrder(Order order) { this.order = order; }
    public Produs getProdus() { return produs; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Pentru afisare in tabel
    public String getNumeProdus() { return produs.getNume(); }
    public double getPretUnitar() { return produs.getPret(); }
}