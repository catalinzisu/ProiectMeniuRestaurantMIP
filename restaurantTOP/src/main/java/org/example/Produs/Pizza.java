package org.example.Produs;

import java.util.ArrayList;
import java.util.List;


public final class Pizza extends Mancare {

    private final String tipBlat;
    private final String tipSos;
    private final List<String> toppinguri;

    private Pizza(Builder builder) {

        super(builder.nume,
                        builder.calculPretFinal(),
                Categorie.FelPrincipal,
                500,
                builder.isVegan);

        this.tipBlat = builder.tipBlat;
        this.tipSos = builder.tipSos;
        this.toppinguri = builder.toppinguri;
    }

    @Override
    public String toString() {
        return String.format("Pizza: %s (Blat: %s, Sos: %s, Topping: %s) - %.2f RON",
                getNume(), tipBlat, tipSos, toppinguri, getPret());
    }

    public static class Builder {

        private final String tipBlat;
        private final String tipSos;

        private final List<String> toppinguri = new ArrayList<>();
        private String nume = "Pizza Customizată";
        private final double pretBaza = 25.0;
        private final double pretPerTopping = 3.0;
        private boolean isVegan = true;

        public Builder(String tipBlat, String tipSos) {
            this.tipBlat = tipBlat;
            this.tipSos = tipSos;
        }

        public Builder cuTopping(String topping) {
            this.toppinguri.add(topping);
            // Presupunem că doar anumite topping-uri marchează pizza ca non-vegană
            if (topping.equalsIgnoreCase("Salam") || topping.equalsIgnoreCase("Prosciutto") || topping.equalsIgnoreCase("Mozzarella")) {
                this.isVegan = false;
            }
            return this;
        }

        public Builder cuNume(String nume) {
            this.nume = nume;
            return this;
        }

        private double calculPretFinal() {
            return this.pretBaza + (this.toppinguri.size() * this.pretPerTopping);
        }

        public Pizza build() {
            return new Pizza(this);
        }
    }
}