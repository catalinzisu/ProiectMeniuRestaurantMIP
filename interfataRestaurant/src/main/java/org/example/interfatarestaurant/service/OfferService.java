package org.example.interfatarestaurant.service;

import org.example.interfatarestaurant.model.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OfferService {
    public static boolean HAPPY_HOUR_ACTIVE = false;
    public static boolean MEAL_DEAL_ACTIVE = false;
    public static boolean PARTY_PACK_ACTIVE = false;

    // Returnează o listă de "Produse fictive" cu preț negativ reprezentând reducerile
    public List<OrderItem> calculateDiscounts(List<OrderItem> items) {
        List<OrderItem> discounts = new ArrayList<>();

        // 1. HAPPY HOUR: Fiecare a doua băutură -50%
        if (HAPPY_HOUR_ACTIVE) {
            List<OrderItem> drinks = items.stream()
                    .filter(i -> i.getProdus() instanceof Bautura)
                    .collect(Collectors.toList());

            int totalDrinksCount = drinks.stream().mapToInt(OrderItem::getQuantity).sum();
            int discountsToApply = totalDrinksCount / 2;

            if (discountsToApply > 0) {
                // Simplificare: luam un preț mediu sau aplicăm la ultima băutură adăugată
                // Corect ar fi sortarea. Aplicăm la prima băutură găsită pentru demo.
                double discountVal = drinks.get(0).getProdus().getPret() * 0.5 * discountsToApply;
                Produs dProd = new Bautura("DISCOUNT: Happy Hour", -discountVal, Categorie.BauturaRacoritoare, 0, false);
                discounts.add(new OrderItem(dProd, 1));
            }
        }

        // 2. PARTY PACK: La 4 Pizza, cea mai ieftină gratuită
        if (PARTY_PACK_ACTIVE) {
            List<OrderItem> pizzas = items.stream()
                    .filter(i -> i.getProdus().getNume().toLowerCase().contains("pizza"))
                    .collect(Collectors.toList());

            int totalPizza = pizzas.stream().mapToInt(OrderItem::getQuantity).sum();
            if (totalPizza >= 4) {
                double minPrice = pizzas.stream()
                        .map(OrderItem::getProdus)
                        .mapToDouble(Produs::getPret)
                        .min().orElse(0);

                Produs dProd = new Mancare("DISCOUNT: Party Pack (Pizza Gratis)", -minPrice, Categorie.FelPrincipal, 0, false);
                discounts.add(new OrderItem(dProd, 1));
            }
        }

        // 3. MEAL DEAL: Pizza -> Cel mai ieftin desert -25%
        if (MEAL_DEAL_ACTIVE) {
            boolean hasPizza = items.stream().anyMatch(i -> i.getProdus().getNume().toLowerCase().contains("pizza"));
            List<OrderItem> deserts = items.stream()
                    .filter(i -> i.getProdus().getCategorie() == Categorie.Desert)
                    .collect(Collectors.toList());

            if (hasPizza && !deserts.isEmpty()) {
                double minDesertPrice = deserts.stream()
                        .map(OrderItem::getProdus)
                        .mapToDouble(Produs::getPret)
                        .min().orElse(0);

                Produs dProd = new Mancare("DISCOUNT: Meal Deal (Desert -25%)", -(minDesertPrice * 0.25), Categorie.Desert, 0, false);
                discounts.add(new OrderItem(dProd, 1));
            }
        }

        return discounts;
    }
}