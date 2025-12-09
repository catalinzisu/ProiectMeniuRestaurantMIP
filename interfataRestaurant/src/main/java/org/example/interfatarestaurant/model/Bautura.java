package org.example.interfatarestaurant.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BAUTURA")
public class Bautura extends Produs {
    private int volum;
    private boolean alcoholic;

    public Bautura() {}
    public Bautura(String nume, double pret, Categorie categorie, int volum, boolean alcoholic) {
        super(nume, pret, categorie);
        this.volum = volum;
        this.alcoholic = alcoholic;
    }



    @Override
    public String toString() { return super.toString() + " (" + volum + "ml)"; }

    public boolean isAlcoholic() {
        return alcoholic;
    }

    public int getVolum() {
        return volum;
    }

    public void setVolum(int volum) {
        this.volum = volum;
    }

    public void setAlcoholic(boolean alcoholic) {
        this.alcoholic = alcoholic;
    }
}