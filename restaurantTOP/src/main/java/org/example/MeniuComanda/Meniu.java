package org.example.MeniuComanda;


import org.example.Produs.Categorie;
import org.example.Produs.Mancare;
import org.example.Produs.Produs;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class Meniu {

    private final List<Produs> listaProduse;
    public Meniu(List<Produs> listaProduse) {
        this.listaProduse = listaProduse;
    }

    public List<Produs> getProduseByCategorie(Categorie categorie) {
        return listaProduse.stream()
                .filter(produs -> produs.getCategorie() == categorie)
                .collect(Collectors.toList());
    }

    // --- AICI SUNT MODIFICĂRILE ---
    // 1. Tipul returnat este acum List<Mancare> în loc de List<Food>
    public List<Mancare> getPreparateVegetarieneSortate() {
        return listaProduse.stream()
                // 2. Verificăm 'instanceof Mancare'
                .filter(produs -> produs instanceof Mancare)
                // 3. Facem cast la 'Mancare'
                .map(produs -> (Mancare) produs)
                // 4. Filtrăm folosind metoda din clasa Mancare (presupunând că se numește isVegan)
                .filter(mancare -> mancare.isVegetarian())
                .sorted(Comparator.comparing(Produs::getNume))
                .collect(Collectors.toList());
    }

    public OptionalDouble getPretMediuDesert() {
        return listaProduse.stream()
                .filter(produs -> produs.getCategorie() == Categorie.Desert)
                .mapToDouble(Produs::getPret)
                .average();
    }

    public boolean hasProdusScump() {
        return listaProduse.stream()
                .anyMatch(produs -> produs.getPret() > 100.0);
    }

    public Optional<Produs> cautaProdus(String nume) {
        return listaProduse.stream()
                .filter(produs -> produs.getNume().equalsIgnoreCase(nume))
                .findFirst();
    }

    public List<Produs> getListaProduse() {
        return listaProduse;
    }
}