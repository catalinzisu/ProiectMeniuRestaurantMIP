package org.example.interfatarestaurant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Masa 1, Masa 2...
    private boolean occupied;

    public RestaurantTable() {}
    public RestaurantTable(String name) {
        this.name = name;
        this.occupied = false;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }

    @Override
    public String toString() { return name + (occupied ? " (Ocupat)" : " (Liber)"); }
}