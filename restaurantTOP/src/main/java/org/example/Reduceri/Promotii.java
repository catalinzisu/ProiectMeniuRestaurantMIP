package org.example.Reduceri;

import org.example.Produs.Categorie;
import org.example.Produs.Produs;
import org.example.Produs.Mancare;
import org.example.Produs.Bautura;
import org.example.MeniuComanda.Comanda;

import java.util.Map;

public final class Promotii {

    private Promotii() {}

    public static final ReguliReducere FARA_REDUCERE = (c) -> 0.0;

    public static final ReguliReducere REDUCERE_ALCOOL_20_PERCENT = (c) -> {
        double reducere = 0.0;
        for (Map.Entry<Produs, Integer> entry : c.getComanda().entrySet()) {
            Produs produs = entry.getKey();
            int cantitate = entry.getValue();

            if (produs instanceof Bautura bautura && bautura.isAlcoholic()) {
                reducere += (produs.getPret() * 0.2) * cantitate;
            }
        }
        return reducere;
    };

    public static final ReguliReducere REDUCERE_VALENTINE_10_PERCENT = (c) -> {
        double totalPret = 0.0;
        for (Map.Entry<Produs, Integer> entry : c.getComanda().entrySet()) {
            totalPret += entry.getKey().getPret() * entry.getValue();
        }
        return totalPret * 0.1;
    };

    public static final ReguliReducere FREE_DRINK_LA_PIZZA = (c) -> {
        String[] drinks = {"Limonada", "Suc de portocale", "Apa minerala", "Bere"};
        int numarBauturiGratis = 0;

        for (Map.Entry<Produs, Integer> entry : c.getComanda().entrySet()) {
            Produs produs = entry.getKey();
            int cantitate = entry.getValue();

            if (produs instanceof Mancare && produs.getNume().toLowerCase().contains("pizza")) {
                numarBauturiGratis += cantitate;
            }
        }

        for (int i = 0; i < numarBauturiGratis; i++) {
            int chosenDrink = (int) (Math.random() * drinks.length);

            Produs bauturaGratis = new Bautura(drinks[chosenDrink], 0.0, Categorie.BauturaRacoritoare, 300);
            c.adaugaProdus(bauturaGratis, 1);
        }

        return 0.0;
    };
}