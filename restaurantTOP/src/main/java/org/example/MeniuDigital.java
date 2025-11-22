package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Produs.Bautura;
import org.example.Produs.Categorie;
import org.example.Produs.Mancare;
import org.example.Produs.Produs;
import org.example.Produs.Pizza;
import org.example.MeniuComanda.Comanda;
import org.example.MeniuComanda.Meniu;
import org.example.Reduceri.Promotii;
import org.example.jsonTOP.ConfigurareApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.List;

public class MeniuDigital {

    public static void main(String[] args) {

        String numeRestaurant = "Restaurant Implicit";
        double tvaConfig = 0.09; // Valoare default

        try {
            ObjectMapper mapper = new ObjectMapper();
            File fisierConfig = new File("Restaurant.json");

            ConfigurareApp config = mapper.readValue(fisierConfig, ConfigurareApp.class);

            numeRestaurant = config.numeRestaurant();
            tvaConfig = config.tva();

            System.out.println("Configurare încărcată cu succes esti talent frate!");

        } catch (FileNotFoundException e) {
            // BAREM: Tratare specifica pentru fisier lipsa
            System.out.println("EROARE: Fișierul de configurare nu a fost găsit, logic trebuie sa-l cauti! Se folosesc valori default <3.");
        } catch (JsonProcessingException e) {
            // BAREM: Tratare specifica pentru fisier corupt (JSON invalid)
            System.out.println("EROARE: Fișierul JSON este corupt sau are format greșit, call Zisu ! " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Eroare neașteptată la configurare chiar ca e belea inseamna ca a bubuit tot call 911: " + e.getMessage());
        }

        Comanda.setTVA(tvaConfig);


        System.out.println("==============================================");
        System.out.println("    Meniul Restaurantului " + numeRestaurant);
        System.out.println("==============================================");

        System.out.println("\n--- 🍕 Testare Pizza Builder ---");

        Pizza pizzaCarbonara = new Pizza.Builder("Blat pufos", "Sos alb")
                .cuNume("Pizza Carbonara")
                .cuTopping("Mozzarella")
                .cuTopping("Bacon")
                .cuTopping("Parmezan")
                .build();

        Pizza pizzaVegetariana = new Pizza.Builder("Blat integral", "Sos de rosii")
                .cuNume("Pizza Vegetariana")
                .cuTopping("Ciuperci")
                .cuTopping("Ardei")
                .cuTopping("Masline")
                .cuTopping("Porumb")
                .build();

        System.out.println(pizzaCarbonara);
        System.out.println(pizzaVegetariana);

        System.out.println("\n--- 📊 Testare Meniu și Streams ---");

        List<Produs> produse = List.of(
                new Mancare("Supa Crema de Ciuperci", 25.0, Categorie.Aperitive, 300, true),
                new Mancare("Platou Branzeturi", 55.0, Categorie.Aperitive, 400, true),
                pizzaCarbonara,
                pizzaVegetariana,
                new Mancare("Somon la Gratar", 75.0, Categorie.FelPrincipal, 250, false),
                new Mancare("T-Bone Steak", 130.0, Categorie.FelPrincipal, 500, false),
                new Mancare("Papanasi", 30.0, Categorie.Desert, 200, true),
                new Bautura("Fresh de Portocale", 18.0, Categorie.BauturaRacoritoare, 350),
                new Bautura("Pahar Vin Alb", 22.0, Categorie.BauturaAlcoolica, 150)
        );

        Meniu meniu = new Meniu(produse);

        System.out.println("\nAfisare Deserturi:");
        meniu.getProduseByCategorie(Categorie.Desert)
                .forEach(System.out::println);

        System.out.println("\nPreparate vegetariene sortate:");
        meniu.getPreparateVegetarieneSortate()
                .forEach(System.out::println);

        System.out.println("\nPreț mediu Desert:");
        meniu.getPretMediuDesert()
                .ifPresent(medie -> System.out.printf("Media este: %.2f RON\n", medie));

        System.out.println("\nExistă produs > 100 RON?");
        System.out.println(meniu.hasProdusScump() ? "DA (T-Bone Steak)" : "NU");

        System.out.println("\nCăutare sigură:");

        Optional<Produs> gasit = meniu.cautaProdus("Papanasi");
        gasit.ifPresent(produs -> System.out.println("Găsit: " + produs));

        Optional<Produs> negasit = meniu.cautaProdus("Clatite");
        negasit.ifPresentOrElse(
                produs -> System.out.println("Găsit: " + produs),
                () -> System.out.println("Produsul 'Clatite' nu a fost găsit.")
        );

        System.out.println("\n--- 💰 Testare Comandă și Reduceri ---");

        Mancare comanda_somon = new Mancare("Somon la Gratar", 75.0, Categorie.FelPrincipal, 250, false);
        Bautura comanda_vin = new Bautura("Pahar Vin Alb", 22.0, Categorie.BauturaAlcoolica, 150);

        Comanda comandaClient = new Comanda();


        comandaClient.adaugaProdus(comanda_somon, 1);
        comandaClient.adaugaProdus(comanda_vin, 2);

        System.out.println("Comanda conține: 1x Somon la Gratar, 2x Pahar Vin Alb.");

        double totalBrut = comandaClient.calculeazaTotal(Promotii.FARA_REDUCERE);
        double totalRedus = comandaClient.calculeazaTotal(Promotii.REDUCERE_ALCOOL_20_PERCENT);

        System.out.printf("Total de plată (fără reducere, cu TVA %.0f%%): %.2f RON\n", tvaConfig*100, totalBrut);
        System.out.printf("Total de plată (cu 20%% reducere la alcool, cu TVA %.0f%%): %.2f RON\n", tvaConfig*100, totalRedus);


        System.out.println("\n--- 💾 Export Meniu în JSON ---");
        try {
            ObjectMapper mapperExport = new ObjectMapper();

            mapperExport.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);

            File fisierExport = new File("meniu_export.json");

            mapperExport.writeValue(fisierExport, meniu.getListaProduse());

            System.out.println("Meniul a fost exportat cu succes în meniu_export.json, se bucura Maria maxim!");
        } catch (Exception e) {
            System.out.println("Eroare la export, trebuie sa call Zisu ASAP: " + e.getMessage());
        }

    }
}