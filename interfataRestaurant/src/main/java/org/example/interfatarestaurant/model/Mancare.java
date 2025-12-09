package org.example.interfatarestaurant.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MANCARE")
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
    public boolean isVegetarian() { return vegetarian; }

    @Override
    public String toString() { return super.toString() + " (" + gramaj + "g)"; }

    public void setGramaj(int gramaj) {
        this.gramaj = gramaj;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }
}