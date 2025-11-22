package org.example.Produs;

public final class Bautura extends Produs {
    private int volum; // in ml
    public Bautura(String nume, double pret, Categorie categorie, int volum) {
        super(nume, pret, categorie);
        this.volum = volum;
    }


    public int getVolum() {
        return volum;
    }

    public void setVolum(int volum) {
        this.volum = volum;
    }

    public boolean isAlcoholic() {
        return getCategorie() == Categorie.BauturaAlcoolica;
    }

    @Override
    public String toString() {
        return "Bautura{" +
                "volum=" + volum + "categorie=" + getCategorie() +
                '}';
    }
}
