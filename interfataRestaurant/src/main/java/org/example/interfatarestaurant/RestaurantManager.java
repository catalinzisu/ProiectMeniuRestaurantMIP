package org.example.interfatarestaurant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Important pentru Date
import org.example.interfatarestaurant.model.Produs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class RestaurantManager {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule()); // Fix pentru date
    }

    public static RestaurantConfiguration incarcaConfigurare(String fisier) {
        try {
            Path cale = Paths.get(fisier);
            if (!Files.exists(cale)) return new RestaurantConfiguration("Restaurant Default", 0.19);
            return mapper.readValue(cale.toFile(), RestaurantConfiguration.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new RestaurantConfiguration("Restaurant Default", 0.19);
        }
    }

    // METODA NOUA PENTRU SALVARE SETARI (OFERTE)
    public static void salveazaConfigurare(RestaurantConfiguration config, String fisier) {
        try {
            mapper.writeValue(Paths.get(fisier).toFile(), config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportaMeniu(List<Produs> produse, String fisierDestinatie) throws Exception {
        mapper.writeValue(Paths.get(fisierDestinatie).toFile(), produse);
    }

    public static List<Produs> importaMeniu(String fisierSursa) throws Exception {
        return mapper.readValue(Paths.get(fisierSursa).toFile(), new TypeReference<List<Produs>>() {});
    }
}