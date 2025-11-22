package org.example.Produs;


public sealed abstract class Produs permits Bautura, Mancare {
    private String nume;
    private double pret;
    private Categorie categorie;

    public Produs(String nume, double pret, Categorie categorie) {
        this.nume = nume;
        this.pret = pret;
        this.categorie = categorie;
    }

    public String getNume() {
        return nume;
    }

    public double getPret() {
        return pret;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setPret(double pret) {
        this.pret = pret;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    @Override
    public String toString() {
        return "Produs{" +
                "nume='" + nume + '\'' +
                ", pret=" + pret +
                ", categorie=" + categorie +
                '}';
    }
}
