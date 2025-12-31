package org.example.interfatarestaurant.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("bautura")
public class Bautura extends Produs {
    private double volum;
    private boolean alcoholic;

    public Bautura() {}

    public Bautura(String nume, double pret, Categorie categorie, double volum, boolean alcoholic) {
        super(nume, pret, categorie);
        this.volum = volum;
        this.alcoholic = alcoholic;
    }

    public double getVolum() { return volum; }
    public void setVolum(double volum) { this.volum = volum; }

    public boolean isAlcoholic() { return alcoholic; }
    public void setAlcoholic(boolean alcoholic) { this.alcoholic = alcoholic; }
}