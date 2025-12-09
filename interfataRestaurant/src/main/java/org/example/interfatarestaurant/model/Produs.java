package org.example.interfatarestaurant.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

@Entity
@Table(name = "produse")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tip_produs", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Mancare.class, name = "mancare"),
        @JsonSubTypes.Type(value = Bautura.class, name = "bautura")
})
public abstract class Produs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nume;
    private double pret;
    @Enumerated(EnumType.STRING)
    private Categorie categorie;

    public Produs() {}
    public Produs(String nume, double pret, Categorie categorie) {
        this.nume = nume;
        this.pret = pret;
        this.categorie = categorie;
    }

    public String getNume() { return nume; }
    public double getPret() { return pret; }
    public Categorie getCategorie() { return categorie; }
    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    @Override
    public String toString() { return nume; }
}