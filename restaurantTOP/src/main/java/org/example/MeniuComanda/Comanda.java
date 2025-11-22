package org.example.MeniuComanda;

import org.example.Produs.Produs;
import org.example.Reduceri.ReguliReducere;

import java.util.HashMap;
import java.util.Map;

public class Comanda {

    private final Map<Produs, Integer> continutComanda;
    private static double TVA=0.0;
    public Comanda() {
        this.continutComanda = new HashMap<>();
    }

    public static void setTVA(double TVA) {
        Comanda.TVA = TVA;
    }

    public static double getTVA() {
        return TVA;
    }

    public void adaugaProdus(Produs produs, int cantitate) {
        this.continutComanda.merge(produs, cantitate, (valoareVeche, cantitateNoua) -> valoareVeche + cantitateNoua);
    }

    public Map<Produs, Integer> getComanda() {
        return continutComanda;
    }

    public double calculeazaTotal(ReguliReducere reguliReducere) {
        double reducere = reguliReducere.aplicaReducere(this);
        double totalFaraTVA = 0.0;
        for (Map.Entry<Produs, Integer> entry : continutComanda.entrySet()) {
            Produs produs = entry.getKey();
            Integer cantitate = entry.getValue();
            totalFaraTVA += produs.getPret() * cantitate;
        }
        double totalCuTVA = (totalFaraTVA - reducere) * (1 + TVA);
        return totalCuTVA;
    }
}