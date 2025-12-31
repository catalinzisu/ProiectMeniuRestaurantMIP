package org.example.interfatarestaurant.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("mancare")
public class Mancare extends Produs {
    private int gramaj;
    private boolean vegetarian;

    public Mancare() {}

    public Mancare(String nume, double pret, Categorie categorie, int gramaj, boolean vegetarian) {
        super(nume, pret, categorie);
        this.gramaj = gramaj;
        this.vegetarian = vegetarian;
    }

    public int getGramaj() { return gramaj; }
    public void setGramaj(int gramaj) { this.gramaj = gramaj; }

    public boolean isVegetarian() { return vegetarian; }
    public void setVegetarian(boolean vegetarian) { this.vegetarian = vegetarian; }
}