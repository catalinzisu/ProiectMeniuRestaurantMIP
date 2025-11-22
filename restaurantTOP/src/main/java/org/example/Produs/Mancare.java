package org.example.Produs;

public non-sealed class Mancare extends Produs{
    private int gramaj; // in grame
    private boolean isVegetarian;
    public Mancare(String nume, double pret, Categorie categorie, int gramaj, boolean isVegetarian) {
        super(nume, pret, categorie);
        this.gramaj = gramaj;
        this.isVegetarian = isVegetarian;
    }


    public int getGramaj() {
        return gramaj;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }


    public void setGramaj(int gramaj) {
        this.gramaj = gramaj;
    }

    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    @Override
    public String toString() {
        String stareVegetarian = isVegetarian ? "Vegetarian" : "Carnivor";

        return String.format("Mancare: %s [Gramaj: %dg, %s] - %.2f RON",
                getNume(),
                gramaj,
                stareVegetarian,
                getPret()
        );
    }
}
