package org.example.interfatarestaurant.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tip_produs", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Mancare.class, name = "mancare"),
        @JsonSubTypes.Type(value = Bautura.class, name = "bautura")
})
public abstract class Produs implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nume;
    private double pret;

    @Enumerated(EnumType.STRING)
    private Categorie categorie;

    // --- CAMPUL NOU ---
    private String descriere;

    public Produs() {}

    public Produs(String nume, double pret, Categorie categorie) {
        this.nume = nume;
        this.pret = pret;
        this.categorie = categorie;
    }

    // Getters si Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public double getPret() { return pret; }
    public void setPret(double pret) { this.pret = pret; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    // --- GETTER SI SETTER PENTRU DESCRIERE ---
    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }

    @Override
    public String toString() {
        return nume + " (" + pret + " RON)";
    }
}